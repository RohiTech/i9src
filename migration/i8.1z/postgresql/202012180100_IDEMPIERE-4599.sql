-- Dec 18, 2020, 6:19:26 AM MYT
UPDATE AD_Process SET IsActive='N',Updated=TO_TIMESTAMP('2020-12-18 06:19:26','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Process_ID=208
;

SELECT register_migration_script('202012180100_IDEMPIERE-4599.sql') FROM dual
;

