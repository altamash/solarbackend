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
