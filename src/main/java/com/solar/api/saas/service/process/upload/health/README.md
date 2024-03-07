## Bulk Upload Validations
#### Common
1. Check required fields in header
2. Required fields must not be empty
3. Check insert and update actions 
4. Parse format of field values as field|format
5. Compare values with portal attribute values as field|portal attribute name
6. Compare values with defined values as field|defined values

#### User
1. user_name must be unique for action INSERT
2. acct_id must be empty for INSERT
3. Customer must exist for UPDATE
4. acct_id must be present for UPDATE
5. Check multiple occurrences of user in file

#### Address
1. address_id must be empty for INSERT
2. address_id must be present for UPDATE
3. In case of INSERT alias must be valid for respective user

#### Payment Info
1. payment_info_id must be empty for INSERT
2. payment_info_id must be present for UPDATE
3. In case of INSERT payment_src_alias must be valid for respective user
4. payment_source must be from system values

#### Customer Subscription
1. Required codes for subscriptionMapping type must be present
2. Format according to measure_definition
3. Active and terminated subscriptions cannot be edited
4. Check subscriptionMapping start date which can only be after minimum one month of garden start date (CSGSDT)
5. Compare PSRC with payment info alias of respective account and SADD with address alias of respective account
6. Cannot exceed garden size (GNSIZE) available capacity with active/inactive subscriptions
7. Premise number must be unique in garden
8. Check multiple occurrences of pn in file