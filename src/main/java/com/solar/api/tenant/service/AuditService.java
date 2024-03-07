package com.solar.api.tenant.service;

import com.solar.api.ETypePackage;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.tenant.mapper.AuditResponse;
import com.solar.api.tenant.model.AuditResponseWrapper;
import com.solar.api.tenant.model.CustomRevisionEntity;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.WordUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.ListProxy;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UserService userService;

    public AuditResponseWrapper getAllActivities(int pageNumber, Integer pageSize, String sort, String type, Long id,
                                                 Long userId, String changedPropertiesCSV, String revisionDate,
                                                 Integer daysBefore,
                                                 String revisionStartDate, String revisionEndDate,
                                                 String propertyName,
                                                 String propertyValue,
                                                 Boolean includeAdditions) throws
            ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
            ParseException, NoSuchFieldException {
        Class<?> clazz = Class.forName(ETypePackage.get(type).getPakage());
        String userName = null;
        if (userId != null) {
            userName = userService.findById(userId).getUserName();
        }
        Pair<Long, Long> startEndTimestamps = getStartEndTimestamps(revisionDate, daysBefore, revisionStartDate,
                revisionEndDate);
        AuditReader reader = AuditReaderFactory.get(em);
        AuditQuery query = composeAuditQuery(reader, clazz, pageNumber, pageSize, sort, id, changedPropertiesCSV,
                userName,
                startEndTimestamps.getKey(), startEndTimestamps.getValue(), propertyName, propertyValue,
                includeAdditions);
        List<Object[]> results = query.getResultList();
        List<AuditResponse> auditResponses = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            AuditResponse.AuditResponseBuilder response = AuditResponse.builder();
            Object obj = clazz.cast(results.get(i)[0]);
            CustomRevisionEntity revEntity = (CustomRevisionEntity) results.get(i)[1];
            RevisionType revType = (RevisionType) results.get(i)[2];
            Set<String> properties = (Set<String>) results.get(i)[3];
            Long objectId = (Long) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(obj);
            response.id(objectId);
            response.revision(revEntity.getId());
            response.timestamp(revEntity.getTimestamp());
            response.dateTime(dateTimeFormat.format(new Date(revEntity.getTimestamp())));
            response.userName(revEntity.getUserName());
            response.fullName(revEntity.getFullName());
            response.clientIp(revEntity.getClientIp());
            response.operation(revType.toString());
            List<AuditResponse.PropertyValue> propertyValues = new ArrayList<>();
            for (String property : properties) {
                if ("updatedAt".equals(property)) {
                    continue;
                }
                AuditResponse.PropertyValue propValue = new AuditResponse.PropertyValue();
                propValue.setPropertyName(property);
                Object value = getPropertyValue(property, obj);
                propValue.setValue(value);
                Object prevObj = getPreviousVersion(reader, clazz, objectId, revEntity.getId());
                if (prevObj != null) {
                    propValue.setPreviousValue(getPropertyValue(property, prevObj));
                }
                propertyValues.add(propValue);
            }
            response.changedProperties(propertyValues);
            auditResponses.add(response.build());
        }
        return AuditResponseWrapper.builder()
                .auditResponses(auditResponses)
                .total(getTotal(clazz, type, userName, id, startEndTimestamps.getKey(), startEndTimestamps.getValue()
                        , changedPropertiesCSV, propertyName, propertyValue, includeAdditions))
                .build();
    }

    private Pair<Long, Long> getStartEndTimestamps(String revisionDateString, Integer daysBefore,
                                                   String revisionStartDateString,
                                                   String revisionEndDateString) throws ParseException {
        Long startTimestamp = null, endTimestamp = null;
        if (revisionDateString != null) {
            Date revisionDate = dateFormat.parse(revisionDateString);
            startTimestamp = Utility.getStartOfDate(revisionDate).getTime();
            endTimestamp = Utility.getEndOfDate(revisionDate).getTime();
        } else if (daysBefore != null) {
            Date now = new Date();
            startTimestamp = Utility.getStartOfDate(Utility.deductDays(now, daysBefore)).getTime();
            endTimestamp = Utility.getEndOfDate(now).getTime();
        } else if (revisionStartDateString != null && revisionEndDateString != null) {
            Date revisionStartDate = dateFormat.parse(revisionStartDateString);
            Date revisionEndDate = dateFormat.parse(revisionEndDateString);
            startTimestamp = Utility.getStartOfDate(revisionStartDate).getTime();
            endTimestamp = Utility.getEndOfDate(revisionEndDate).getTime();
        }
        return new ImmutablePair<>(startTimestamp, endTimestamp);
    }

    private AuditQuery composeAuditQuery(AuditReader reader, Class<?> clazz, int pageNumber, Integer pageSize,
                                         String sort, Long id,
                                         String changedPropertiesCSV, String userName, Long startTimestamp,
                                         Long endTimestamp, String propertyName, String propertyValue,
                                         Boolean includeAdditions) throws NoSuchFieldException {
        pageSize = pageSize == null ? SaasSchema.PAGE_SIZE : pageSize;
        AuditQuery query = reader.createQuery().forRevisionsOfEntityWithChanges(clazz, true);
        if (userName != null) {
            query.add(AuditEntity.revisionProperty("userName").eq(userName));
        }
        if (id != null) {
//            query.add(AuditEntity.property(em.getMetamodel().entity(clazz).getId(Long.class).getName()).eq(id));
            query.add(AuditEntity.id().eq(id));
        }
        if (startTimestamp != null && endTimestamp != null) {
            query.add(AuditEntity.revisionProperty("timestamp").between(startTimestamp, endTimestamp));
        }
        if (sort != null) {
            query.addOrder(AuditEntity.property(sort).asc());
        } else {
            query.addOrder(AuditEntity.revisionNumber().desc());
        }
        if (changedPropertiesCSV != null) {
            Arrays.stream(changedPropertiesCSV.split(",")).map(p -> p.trim()).collect(Collectors.toList())
                    .forEach(property -> query.add(AuditEntity.property(property).hasChanged()));
            query.add(AuditEntity.revisionType().ne(RevisionType.ADD));
        }
        if (propertyName != null) {
            /*Class fieldType = clazz.getDeclaredField(propertyName).getType();
            if (fieldType == Double.class) {
                propertyValue = Double.parseDouble((String) propertyValue);
            } else if (fieldType == Long.class) {
                propertyValue = Long.parseLong((String) propertyValue);
            } else if (fieldType == List.class) {
                propertyValue = Arrays.asList(propertyValue);
            }*/
//            propertyValue = getPropertyValue(clazz, propertyName, propertyValue);
//            if (fieldType == String.class) {
//                query.add(AuditEntity.property(propertyName).ilike(propertyValue));
//            } else {
            query.add(AuditEntity.property(propertyName).eq(getPropertyValue(clazz, propertyName, propertyValue)));
//            }
        }
        if (includeAdditions != null && includeAdditions.booleanValue() != true) {
            query.add(AuditEntity.revisionType().ne(RevisionType.ADD));
        }
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        return query;
    }

    private String getPrimaryKeyName(Class clazz) {
        return em.getMetamodel().entity(clazz).getId(Long.class).getName();
    }

    private Object getPropertyValue(Class<?> clazz, String propertyName, String propertyValue) throws NoSuchFieldException {
        Class fieldType = clazz.getDeclaredField(propertyName).getType();
        Object value = propertyValue;
        if (fieldType == Double.class) {
            value = Double.parseDouble(propertyValue);
        } else if (fieldType == Long.class) {
            value = Long.parseLong(propertyValue);
        } else if (fieldType == List.class) {
            value = Arrays.asList(propertyValue);
        }
        return value;
    }

    private static Object getPropertyValue(String propertyName, Object object) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        Method method = object.getClass().getDeclaredMethod("get" + WordUtils.capitalize(propertyName));
        if (Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 0
                && method.getReturnType() != void.class) {
            Object value = method.invoke(object);
            if (value != null) {
                if ("billingDetails".equals(propertyName)) {
                    return ((Set) value).stream()
                            .map(v -> ((BillingDetail) v).getId()).collect(Collectors.toList()).toString();
                } else if ("paymentTransactionDetails".equals(propertyName)) {
                    return ((ListProxy) value).stream()
                            .map(v -> ((PaymentTransactionDetail) v).getPayDetId()).collect(Collectors.toList()).toString();
                }
                return value;
            }
        }
        return null;
    }

    public static Object getPreviousVersion(AuditReader reader, Class clazz, Long objectId, int currentRev) {
        Number priorRevision = (Number) reader.createQuery()
                .forRevisionsOfEntity(clazz, false, true)
                .addProjection(AuditEntity.revisionNumber().max())
                .add(AuditEntity.id().eq(objectId))
                .add(AuditEntity.revisionNumber().lt(currentRev))
                .getSingleResult();

        if (priorRevision != null) {
            return reader.find(clazz, objectId, priorRevision);
        } else {
            return null;
        }
    }

    private Long getTotal(Class clazz, String type, String userName, Long id, Long startTimestamp, Long endTimestamp,
                          String changedPropertiesCSV, String propertyName, String propertyValue,
                          Boolean includeAdditions) throws NoSuchFieldException {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        type = type.replaceAll(regex, replacement).toLowerCase();
        StringBuilder queryString = new StringBuilder("select count(*) from " + type.toLowerCase() + "_aud t, revinfo" +
                " ri" +
                " where t.rev = ri.id" +
                (userName != null ? " and ri.user_name = :userName" : "") +
                (id != null ? " and t." + getPrimaryKeyName(clazz).replaceAll(regex, replacement).toLowerCase() + " =" +
                        " :id" : "") +
                (startTimestamp != null && endTimestamp != null ? " and ri.timestamp between :startTimestamp and " +
                        ":endTimestamp" : "") +
                (propertyName != null ? " and t." + propertyName.replaceAll(regex, replacement).toLowerCase() + " = " +
                        ":propertyValue" : "") +
                (changedPropertiesCSV != null || includeAdditions != null && includeAdditions.booleanValue() != true
                        ? " and t.revtype != 0" : ""));
        if (changedPropertiesCSV != null) {
            Arrays.stream(changedPropertiesCSV.split(",")).map(p -> p.trim()).collect(Collectors.toList())
                    .forEach(property -> queryString.append(" and t." + property.replaceAll(regex, replacement).toLowerCase() + "_mod = true"));
        }
        Query query = em.createNativeQuery(queryString.toString());
        if (userName != null) {
            query.setParameter("userName", userName);
        }
        if (id != null) {
            query.setParameter("id", id);
        }
        if (startTimestamp != null && endTimestamp != null) {
            query.setParameter("startTimestamp", startTimestamp);
            query.setParameter("endTimestamp", endTimestamp);
        }
        if (propertyName != null) {
            query.setParameter("propertyValue", getPropertyValue(clazz, propertyName, propertyValue));
        }
        return ((BigInteger) query.getSingleResult()).longValue();
    }

}
