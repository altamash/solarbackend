####    Object hookFinder(String hookConstant)
        Object =  hookId, TypeA_enabled, TypeB_enabled (wf_hook_master)
//        checkAMap(hookId, typeA)
        checkBMap(hookId, typeB)

    private checkAMap(hookId, typeA) {

          if typeA is enabled then {

              check in wf_hook_map wkf id if you have type A hook attached then return (wkf_id and list_id)

          }


    private checkBMap(hookId, typeB) {

          if typeB is enabled then {

              check in wf_hook_map wkf id if you have type B hook attached then initiatorWfProcess(wkf_id, hookId, list_id)

          }


    private initiatorWfProcess(wkf_id, hookId, list_id) {
        get typeB_list for emails (e)
            if either e or n records exist
               add entry in Wf_Exec_Process

