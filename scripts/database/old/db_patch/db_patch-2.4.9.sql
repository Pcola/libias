update CROSSIDENTIFICATIONCASES set STATUSID=7 where statusid=9;
delete from status where statusid=9;
DROP TRIGGER LOG_SEQ_TR;