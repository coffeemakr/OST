#!/usr/bin/env python3

import urllib.request
import zipfile
import os.path
from collections import namedtuple
import csv
import sqlite3
import shutil

Station = namedtuple('Station', ['id', 'name', 'type', 'frequence'])

TYPE_IDS = {
    'Zug': 1 << 0,
    'Bus': 1 << 1,
    'Tram': 1 << 2,
    'Schiff': 1 << 3,
    'Metro': 1 << 4,
    'Luftseilbahn': 1 << 5,
    'Zahnradbahn': 1 << 6,
    'Standseilbahn': 1 << 7,
    'Aufzug': 1 << 8
}



conn = sqlite3.connect('timetable.db')
c = conn.cursor()
c.execute('SELECT DISTINCT stop_id from stops');
USED_STOP_IDS = [v[0] for v in c]
conn.close()

ABBRVS = [
    ('La Chaux-de-F', 'La Chaux-de-Fonds'),
    ('Belmont-s.-L.', 'Belmont-sur-Lausanne'),
]

def full_names(name):
    for short_name, long_name in ABBRVS:
        name.replace(short_name, long_name)
    return name


def latin_encoder(latin_csv_data):
    for line in latin_csv_data:
        yield line.decode('latin1')

def read_passenger_frequence():
    frequence_by_id = {}
    filename = 'data/passenger-frequence.csv'
    with open(filename) as csvfile:
        for row in csv.DictReader(csvfile, delimiter=';'):
            ident = row['lod'].rsplit('/', 1)[1]
            frequence_by_id[ident] = int(row['DTV average daily traffic']) 
    return frequence_by_id


FREQUENCE_BY_ID = read_passenger_frequence()

def read_csv(csv_file):
    csv_reader = csv.DictReader(latin_encoder(csv_file), delimiter=',')
    for row in csv_reader:
        id = int(row['Nummer'])
        name = full_names(row['Name'])
        types = row['Verkehrsmittel']
        if str(id) in FREQUENCE_BY_ID:
            frequence = FREQUENCE_BY_ID[str(id)]
        else:
            frequence = 0
        if len(types) == 0:
            print("WARN: Skipping stations without verkehrsmittel: %s" % name)
            continue
        type_num = 0
        types = types.split('_')
        for type_ in types:
            if type_ not in TYPE_IDS:
                print("WARN: Station %s type %s not found" % (name, type_))
                continue
            type_num |= TYPE_IDS[type_]
        station = Station(id, name, type_num, frequence)
        yield station

def write_stations(stations, sqlite_filename):
    if os.path.isfile(sqlite_filename):
        os.unlink(sqlite_filename)
    conn = sqlite3.connect(sqlite_filename)
    fts_table='fts_stations'
    table='stations'
    c = conn.cursor();
    c.execute('''CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US')''')
    c.execute('''INSERT INTO "android_metadata" VALUES ('en_US')''')
    c.execute('''CREATE TABLE "%s" (
            id INTEGER PRIMARY KEY NOT NULL, 
            name text NOT NULL,
            types INTEGER NOT NULL,
            frequency INTEGER NOT NULL
            )''' % (table,))
    c.execute('''CREATE VIRTUAL TABLE "%s" USING fts4(tokenize=simple, content="%s", name)''' % (fts_table, table))
    for station in stations:
        if str(station.id) not in USED_STOP_IDS:
            print("WARN: Skipping name (id %s)" % station.id +  station.name)
            continue
        conn.execute('''INSERT INTO stations VALUES (?, ?, ?, ?)''', (station.id, station.name, station.type, station.frequence))
    c.execute("INSERT INTO %s(%s) VALUES('rebuild')" % (fts_table, fts_table));
    conn.commit()
    conn.close()

url =  'http://data.geo.admin.ch/ch.bav.haltestellen-oev/data.zip'
data_file="data.zip"
csv_zip_file_name='HST_MGMD_2017-05-01_CSV.ZIP'
csv_file_name='HST_MGMD_2017-05-01/Betriebspunkt.csv'
sqlite_filename='app/src/main/assets/stations.db'
if not os.path.isfile(data_file):
    urllib.request.urlretrieve(url,data_file) 

if not os.path.isfile(csv_zip_file_name):
    with zipfile.ZipFile(data_file) as data_zip:
        data_zip.extract(csv_zip_file_name) 

with zipfile.ZipFile(csv_zip_file_name) as csv_zip:
    with csv_zip.open(csv_file_name, 'r') as csv_file:
        write_stations(read_csv(csv_file), sqlite_filename)
