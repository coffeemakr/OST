#!/usr/bin/env python

import sqlite3
import csv
import zipfile

GTFS_SOURCE='data/gtfsfp20172017-08-02.zip'

conn = sqlite3.connect("gtfs-timetable-2.db")
c = conn.cursor();
c.execute('''
    CREATE TABLE agencies (
        agency_id TEXT,
        agency_name TEXT NON NULL,
        agency_url TEXT NON NULL,
        agency_timezone TEXT,
        agency_long TEXT,
        agency_phone TEXT,
        agency_fare_url TEXT,
        agency_email TEXT
    )''')
'''
Route type:
    0: Tram, Streetcar, Light rail. Any light rail or street level system within a metropolitan area.
    1: Subway, Metro. Any underground rail system within a metropolitan area.
    2: Rail. Used for intercity or long-distance travel.
    3: Bus. Used for short- and long-distance bus routes.
    4: Ferry. Used for short- and long-distance boat service.
    5: Cable car. Used for street-level cable cars where the cable runs beneath the car.
    6: Gondola, Suspended cable car. Typically used for aerial cable cars where the car is suspended from the cable.
    7: Funicular. Any rail system designed for steep inclines.
'''

#Field = namedtuple('Field', ['name', 'required', 'type_name', 'type_fnc'])

CALENDAR_FIELDS = ['service_id', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday', 'start_date', 'end_date']

c.execute('''CREATE TABLE calendar(
        service_id TEXT NOT NULL,
        monday INTEGER NOT NULL,
        tuesday INTEGER NOT NULL,
        wednesday INTEGER NOT NULL,
        thursday INTEGER NOT NULL,
        friday INTEGER NOT NULL,
        saturday INTEGER NOT NULL,
        sunday INTEGER NOT NULL,
        start_date TEXT NOT NULL,
        end_date TEXT NOT NULL
        )''')

ROUTE_FIELDS = [
        'route_id', 
        'agency_id',
        'route_short_name', 
        'route_long_name', 
        'route_desc', 
        'route_type', 
        'route_url', 
        'route_color', 
        'route_text_color']


c.execute('''CREATE TABLE routes(
        route_id TEXT NOT NULL PRIMARY KEY,
        agency_id TEXT,
        route_short_name TEXT NOT NULL,
        route_long_name TEXT NOT NULL,
        route_desc TEXT,
        route_type INTEGER NOT NULL,
        route_url TEXT,
        route_color TEXT,
        route_text_color TEXT,
        FOREIGN KEY (agency_id) REFERENCES agency(agency_id)
        )''');

TRIP_FIELDS = [
        'route_id',
        'service_id',
        'trip_id',
        'trip_headsign',
        'trip_short_name',
        'direction_id',
        'block_id',
        'shape_id',
        'wheelchair_accessible',
        'bikes_allowed'
        ]
c.execute('''CREATE TABLE trips (
        route_id TEXT NOT NULL,
        service_id TEXT NOT NULL REFERENCES calendar(service_id),
        trip_id TEXT NOT NULL PRIMARY KEY,
        trip_headsign TEXT,
        trip_short_name TEXT,
        direction_id INTEGER,
        block_id TEXT,
        shape_id TEXT,
        wheelchair_accessible INTEGER  DEFAULT 0,
        bikes_allowed INTEGER DEFAULT 0,
        FOREIGN KEY (route_id) REFERENCES route(route_id)
        )''')

STOP_FIELDS = [
        'stop_id',
        'stop_code',
        'stop_name',
        'stop_desc',
        'stop_lat',
        'stop_lon',
        'zone_id',
        'stop_url',
        'location_type',
        'parent_station',
        'stop_timezone',
        'wheelchair_boarding'
        ]

c.execute('''CREATE TABLE stops (
        stop_id TEXT NOT NULL PRIMARY KEY,
        stop_code TEXT,
        stop_name TEXT NOT NULL,
        stop_desc TEXT,
        stop_lat REAL,
        stop_lon REAL,
        zone_id TEXT,
        stop_url TEXT,
        location_type INTEGER,
        parent_station TEXT,
        stop_timezone TEXT,
        wheelchair_boarding INTEGER DEFAULT 0
        )
        ''')

STOP_TIME_FIELDS = [
        'trip_id',
        'arrival_time',
        'departure_time',
        'stop_id',
        'stop_sequence',
        'stop_headsign',
        'pickup_type',
        'drop_off_type',
        'shape_dist',
        'timepoint'
        ]

c.execute('''CREATE TABLE stop_times (
        trip_id TEXT NOT NULL REFERENCES trips(trip_id),
        arrival_time TEXT NOT NULL,
        departure_time TEXT NOT NULL,
        stop_id TEXT NON NULL REFERENCES stops(stop_id),
        stop_sequence INTEGER NOT NULL,
        stop_headsign TEXT,
        pickup_type INTEGER,
        drop_off_type INTEGER,
        shape_dist_traveled REAL,
        timepoint INTEGER
        )''')
        

def utf_decoder(lines):
    for line in lines:
        yield line.decode('utf-8')


def fill_table(gtfs_filename, filename, table, fields):
    with zipfile.ZipFile(gtfs_filename) as z:
        with z.open(filename) as trips:
            first_line = trips.readline().decode('utf-8').strip()
            if first_line.startswith('\ufeff'):
                first_line = first_line[1:]
            query ='INSERT INTO ' + table + ' VALUES (' + ', '.join(len(fields) * ['?']) + ')' 
            for trip in csv.DictReader(utf_decoder(trips), fieldnames=first_line.split(',')):
                values = []
                for field in fields:
                    values.append(trip.get(field, None))
                try:
                    c.execute(query, values)
                except sqlite3.IntegrityError as e:
                    print(trip)
                    print("values: %s " % str(dict(zip(fields, values))))
                    raise e
                    #continue


print("Filling calendar")
fill_table(GTFS_SOURCE, 'calendar.txt', 'calendar', CALENDAR_FIELDS)
print("Filling routes")
fill_table(GTFS_SOURCE, 'routes.txt', 'routes', ROUTE_FIELDS)
conn.commit()
print("Filling trips")
fill_table(GTFS_SOURCE, 'trips.txt', 'trips', TRIP_FIELDS)
conn.commit()
print("Filling stops")
fill_table(GTFS_SOURCE, 'stops.txt', 'stops', STOP_FIELDS)
conn.commit()
print("Filling stop times")
fill_table(GTFS_SOURCE, 'stop_times.txt', 'stop_times', STOP_TIME_FIELDS)
conn.commit()

