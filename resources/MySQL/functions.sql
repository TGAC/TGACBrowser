CREATE DEFINER=`tgacbrowser`@`%` FUNCTION `get_ref_coord`(  id  BigInt,  ref_id BigInt ) RETURNS bigint(20)
 BEGIN
  BEGIN
   DECLARE start_pos BIGINT DEFAULT 0;
   DECLARE temp_id BIGINT DEFAULT 0;
   DECLARE temp_start BIGINT DEFAULT 0;
   DECLARE x BIGINT;
   set x = id;

   sloop:LOOP
    SELECT asm_seq_region_id into temp_id from assembly where cmp_seq_region_id = X;

    if temp_id = ref_id then
     SELECT asm_start INTO temp_start FROM assembly WHERE cmp_seq_region_id = X;
     set start_pos = start_pos + temp_start;
     leave sloop;
    else
     set x = temp_id;
     set start_pos = temp_start;
     ITERATE sloop;
    end if;
   END loop;
   RETURN start_pos;
  END;
 END