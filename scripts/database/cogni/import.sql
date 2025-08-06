connect system/password;

CREATE OR REPLACE DIRECTORY IMAGES AS 'C:\Projects\bamf\bamfface\scripts\database\images';
GRANT READ ON DIRECTORY IMAGES TO cogni;

disconnect;

connect cogni/cogni123;

DECLARE
  numOfExistingImages NUMBER := 0; 
  startId NUMBER := 1;
  numOfImages NUMBER := 2000;
  img  BFILE;
  img1  BFILE := BFILENAME('IMAGES', 'CP_goldschmidt_vladimir.jpg');
  img2  BFILE := BFILENAME('IMAGES', 'CP_goldschmidtova_zdenka.jpg');
  img3  BFILE := BFILENAME('IMAGES', 'CP_panak_branislav.jpg');
  img4  BFILE := BFILENAME('IMAGES', 'CP_rampasekova_alexandra.jpg');
  img5  BFILE := BFILENAME('IMAGES', 'CP_rampasekova_ingrid.jpg');
  img6  BFILE := BFILENAME('IMAGES', 'CP_sreknel_peter.jpg');
  img7  BFILE := BFILENAME('IMAGES', 'Erika_Mustermann_2005.jpg');
  img8  BFILE := BFILENAME('IMAGES', 'Erika_Mustermann_2010.jpg');
  img9  BFILE := BFILENAME('IMAGES', 'ingrid.jpg');
  img10  BFILE := BFILENAME('IMAGES', 'Jaro1.jpg');
  img11  BFILE := BFILENAME('IMAGES', 'Jaro2.jpg');
  img12  BFILE := BFILENAME('IMAGES', 'Jaro3.jpg');
  img13  BFILE := BFILENAME('IMAGES', 'Jaro4.jpg');
  img14  BFILE := BFILENAME('IMAGES', 'Jaro5.JPG');
  img15  BFILE := BFILENAME('IMAGES', 'OP_goldschmidt_vladimir.jpg');
  img16  BFILE := BFILENAME('IMAGES', 'OP_goldschmidtova_zdenka.jpg');
  img17  BFILE := BFILENAME('IMAGES', 'OP_panak_branislav.jpg');
  img18  BFILE := BFILENAME('IMAGES', 'OP_panak_branislav2.jpg');
  img19  BFILE := BFILENAME('IMAGES', 'OP_rampasekova_alexandra.jpg');
  img20  BFILE := BFILENAME('IMAGES', 'OP_rampasekova_ingrid.jpg');
  l_blob BLOB; 
  v_amount   INTEGER;
BEGIN

  SELECT COUNT(1) INTO numOfExistingImages FROM BILD;
  startId := numOfExistingImages+1;
  FOR i IN startId .. numOfExistingImages+numOfImages LOOP
    CASE MOD(i,20)
    WHEN 1 THEN img := img1;
    WHEN 2 THEN img := img2;
    WHEN 3 THEN img := img3;
    WHEN 4 THEN img := img4;
    WHEN 5 THEN img := img5;    
    WHEN 6 THEN img := img6;
    WHEN 7 THEN img := img7;
    WHEN 8 THEN img := img8;
    WHEN 9 THEN img := img9;
    WHEN 10 THEN img := img10;
    WHEN 11 THEN img := img11;
    WHEN 12 THEN img := img12;
    WHEN 13 THEN img := img13;
    WHEN 14 THEN img := img14;
    WHEN 15 THEN img := img15;
    WHEN 16 THEN img := img16;
    WHEN 17 THEN img := img17;
    WHEN 18 THEN img := img18;
    WHEN 19 THEN img := img19;
    ELSE  img := img20;
    END CASE;

    DBMS_LOB.OPEN(img, DBMS_LOB.LOB_READONLY);
    v_amount := DBMS_LOB.GETLENGTH(img);
    INSERT INTO BILD(OID,BILDDATEN,DATE_MODIFIED) 
    values (i,empty_blob(),sysdate()) RETURN bilddaten INTO l_blob;
    DBMS_LOB.LOADFROMFILE(l_blob, img, v_amount);
    DBMS_LOB.CLOSE(img);
  END LOOP;
  COMMIT;
END;
/
disconnect;