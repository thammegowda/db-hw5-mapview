-- Name  : ThammeGowda Narayanaswamy
-- USCID : 2074669439
-- Topic : USC CSCI585 HW5 Solutions


-- delete spatial metadata
 DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='ambulance';
 DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='region';
 DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='pond';
 DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='lion';

-- DROP INDEX
DROP INDEX idx_ambulance;
DROP INDEX idx_region;
DROP INDEX idx_pond;
DROP INDEX idx_lion;

-- Drop tables
DROP TABLE ambulance;
DROP TABLE region;
DROP TABLE pond;
DROP TABLE lion;
--DONE