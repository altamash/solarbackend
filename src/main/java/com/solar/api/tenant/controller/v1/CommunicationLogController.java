package com.solar.api.tenant.controller.v1;

import com.mchange.util.AlreadyExistsException;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.extended.project.CommunicationLogDTO;
import com.solar.api.tenant.service.CommunicationLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("communicationLogController")
@RequestMapping(value = "/communicationLog")
public class CommunicationLogController {

    @Autowired
    CommunicationLogsService communicationLogsService;

    @PostMapping("/add")
    public List<CommunicationLogDTO> add(@RequestParam(value = "file") List<MultipartFile> file,
                                               @RequestParam(value = "level") String level,
                                               @RequestParam(value = "levelId") Long levelId,
                                               @RequestParam(value = "severity") String severity,
                                               @RequestParam(value = "type") String type,
                                               @RequestParam(value = "approvalRequired", required = false) String approvalRequired,
                                               @RequestParam(value = "approver", required = false) Long approver,
                                               @RequestParam(value = "approvalDate", required = false) String approvalDate,
                                               @RequestParam(value = "requester", required = false) String requester,
                                               @RequestParam(value = "message") String message,
                                               @RequestParam(value = "docId", required = false) Long docId,
                                               @RequestParam(value = "recSeqNo", required = false) String recSeqNo,
                                               @RequestHeader("Comp-Key") Long compKey) throws StorageException, IOException, URISyntaxException {

        String[] fileFrags = file.get(0).getOriginalFilename().split("\\.");
        String extension = fileFrags[fileFrags.length-1];
        return null;
//                toDocuLibraryDTOs(docuLibraryService.saveProjectDocument(file, level, levelId, compKey, docuType));
    }

//    @PostMapping("/add")
//    public CommunicationLogDTO add(@RequestBody CommunicationLogDTO communicationLogDTO) throws AlreadyExistsException {
//        return toCommunicationLogDTO(communicationLogsService.addOrUpdate(toCommunicationLog(communicationLogDTO)));
//    }

    @PutMapping("/update")
    public CommunicationLogDTO update(@RequestBody CommunicationLogDTO communicationLogDTO) throws AlreadyExistsException {
        return toCommunicationLogDTO(communicationLogsService.addOrUpdate(toCommunicationLog(communicationLogDTO)));
    }

    @GetMapping("/get/{level}")
    public List<CommunicationLogDTO> findByLevel(@PathVariable String level) {
        return toCommunicationLogDTOs(communicationLogsService.findAllByLevel(level));
    }

    @GetMapping("/get/{level}/{id}")
    public CommunicationLogDTO findByLevelAndLevelId(@PathVariable String level, @PathVariable Long id) {
        return toCommunicationLogDTO(communicationLogsService.findByLevelAndLevelId(level, id));
    }

    @GetMapping("/get")
    public List<CommunicationLogDTO> getAll() {
        return toCommunicationLogDTOs(communicationLogsService.findAll());
    }

    @GetMapping("/get/{id}")
    public CommunicationLogDTO findById(@PathVariable Long id) {
        return toCommunicationLogDTO(communicationLogsService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        communicationLogsService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity deleteAll() {
        communicationLogsService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }


}
