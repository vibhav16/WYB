import MySQLdb
import pickle
file=open('score.pickle','rb')
d=pickle.load(file)
db = MySQLdb.connect(host="localhost", user="root", passwd="", db="wyb")
cur = db.cursor()

for key, value in d.items():
	cur.execute("insert into test (food, rating) values ('{0}', '{1}')".format(key, value))

db.close()
