#!/usr/bin/env python3

import urllib.request
import zipfile
import os.path
from collections import namedtuple
import csv
import sqlite3
import shutil

Station = namedtuple('Station', ['id', 'name'])

def latin_encoder(latin_csv_data):
    for line in latin_csv_data:
        yield line.decode('latin1')

def read_csv(csv_file):
    csv_reader = csv.DictReader(latin_encoder(csv_file), delimiter=',')
    for row in csv_reader:
        id = row['Nummer']
        name = row['Name']
        station = Station(id, name)
        yield station	

def write_stations(stations, sqlite_filename):
    conn = sqlite3.connect(sqlite_filename)
    c = conn.cursor();
    c.execute('''CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US')''')
    c.execute('''INSERT INTO "android_metadata" VALUES ('en_US')''')
    c.execute('''CREATE TABLE stations
    (_id TEXT PRIMARY KEY NOT NULL, name text NOT NULL)''')
    for station in stations:
        conn.execute('''INSERT INTO stations VALUES (?, ?)''', (station.id, station.name))
    conn.commit()
    conn.execute('VACUUM')
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
