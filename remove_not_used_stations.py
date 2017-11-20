import sqlite3
import sys
from collections import defaultdict

FIND_DUPLICATES = '''
	SELECT MIN(c.service_id) as RealID, c2.service_id as DuplicateId
	FROM calendar c join calendar c2
	ON
		c.monday = c2.monday AND
		c.tuesday = c2.tuesday AND
		c.wednesday = c2.wednesday AND
		c.thursday = c2.thursday AND
		c.friday = c2.friday AND
		c.saturday = c2.saturday AND
		c.sunday = c2.sunday AND
		c.start_date = c2.start_date AND
		c.end_date = c2.end_date AND
		c.service_id < c2.service_id
	Group by c2.service_id
        '''



def find_duplicated_dates(conn):
    c = conn.cursor()
    return c.execute(FIND_DUPLICATES)

def remove_duplicate(conn, real_id, duplicated_ids):
    print("Removing %d duplicates"  % len(duplicated_ids))
    ids_format = ','.join(len(duplicated_ids) * ['?'])
    c = conn.cursor()
    real_and_duplicated_ids = list(duplicated_ids)
    real_and_duplicated_ids.insert(0, real_id)
    c.execute("UPDATE trips SET service_id = ? WHERE service_id in (" + ids_format + ")", real_and_duplicated_ids)
    c.execute("DELETE FROM calendar WHERE service_id IN (" + ids_format + ")", duplicated_ids)

if __name__ == '__main__':
    conn = sqlite3.connect(sys.argv[1])
    dict_data = defaultdict(list)
    for real_id, duplicate_id in find_duplicated_dates(conn):
        dict_data[real_id].append(duplicate_id)
    for real_id, duplicated_ids in dict_data.items():
        remove_duplicate(conn, real_id, duplicated_ids)
    conn.commit()
    c= conn.cursor()
    c.execute("VACUUM")
    conn.commit()
    conn.close()
