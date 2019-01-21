'''
Created on 2018. 10. 23.

@author: AreumChoi
'''
import numpy as np
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.svm import LinearSVC
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.multiclass import OneVsRestClassifier
from sklearn.metrics.classification import accuracy_score
import os

X_train=[]
y_train=[]
test=[]
Real_Value=[]

path = os.path.abspath(os.path.join(os.getcwd(), os.pardir))

with open(path+'\CreateCSV\\data_morp.csv', 'r') as reader:
    for line in reader:
        fields = line.strip().split(',')
        X_train.append(fields[0])
        y_train.append(int(fields[1]))

with open(path+'\CreateCSV\\test_morp_normal.csv', 'r') as reader:
    for line in reader:
        fields = line.strip().split(',')
        test.append(fields[0])
        Real_Value.append(int(fields[1]))

        
X_train=np.array(X_train)
y_train=np.array(y_train)

Real_Value = np.array(Real_Value)

target_names = ['Class1', 'Class2','Class3']

classifier = Pipeline([
    ('vectorizer', CountVectorizer()),
    ('tfidf', TfidfTransformer()),
    ('clf', OneVsRestClassifier(LinearSVC()))])

classifier.fit(X_train, y_train)
predicted = classifier.predict(test)
print("SVM")


rumor_count = 0
ad_count=0
for item, labels in zip(test, predicted):
    if labels == 1:
        rumor_count += 1
    elif labels == 2:
        ad_count += 1
    print('%s => %s' % (item, ''.join(target_names[labels])))
    
print("accuracy score : ",accuracy_score(Real_Value, predicted))
print('루머성 확률 : ', (rumor_count/len(test))*100,'%' )
print('광고성 확률 : ', (ad_count/len(test))*100,'%' )