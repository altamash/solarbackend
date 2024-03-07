# Prepaid Billing
***

### 1- Excel Billing credits file to be changed into a temaplate.
* Create a duplicate of Excel Bill credit import.
* Fill columns (Generated Power, Billing Month, Premise No., Locality No., Line code)

### 2 Create Product in Mongo Marketplace

### 3 Create product ( Download in Tenant Schema)

### 4 Create Varient (Solar Garden)

### 5 Create User (Operational Task)

### 6 Create Subscriptions (Operational Task)

### 7 Development of CSG_R_PRE (parser Code for billing)
* On GENERATE
  * Fetch Power generated from imported table to billing detail (__billing code: POWG__)
  * Multiply __POWG__ with __STRE__ (Derivative with condition of customer type), __(POWG * [SRTE] * (1 - NVL (S_DSCP, 0)) * -1__ as __MBILL__ (Adjusted monthly bill) & add to billing detail
  * Also check if last total prepaid amount is different from the current prepaid amount in measure, then add the same difference to __S_REMBAL__
  * Add total prepaid amount (__S_TPRE__) in __rule_execution_log__
  * Add running balance to billing detail (__billing code = RBAL__) from the current value of __S_REMBAL__ from subscriptionMapping, replace if exists.
  * if the bill is net off to 0, mark the bill to __PAID__ and update __S_REMBAL__ measure code in subscriptionMapping to (__RBAL - MBILL__)
  * If the bill is not net to 0 or amount is pending the bill is marked to __INVOICED__ and __S_REMBAL__ is updated to __RBAL - MBILL__, where the value will stand less than 0