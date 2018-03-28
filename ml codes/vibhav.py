import MySQLdb
import pickle
file=open('score.pickle','rb')
d=pickle.load(file)
db = MySQLdb.connect("localhost", "root", "", "wyb")
print("vibhav")
cur = db.cursor()

for key in d:
	c=cur.execute("insert into test (food, rating) values ('{0}', '{1}')".format(key, d[key]))
	db.commit()
db.close()
