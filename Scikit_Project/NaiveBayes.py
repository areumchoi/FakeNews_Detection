'''
Created on 2018. 10. 25.

@author: AreumChoi
'''

from textblob.classifiers import NaiveBayesClassifier
import os
from sklearn.metrics import accuracy_score

train=[]
test=[]
Real_Value=[]

path = os.path.abspath(os.path.join(os.getcwd(), os.pardir))

with open(path+'\CreateCSV\\data_morp.csv', 'r') as reader:
    for line in reader:
        fields = line.strip().split(',')
        train.append(fields)

with open(path+'\CreateCSV\\test_morp_normal.csv', 'r') as reader:
    for line in reader:
        fields = line.strip().split(',')
        test.append(fields[0])
        Real_Value.append(fields[1])

clf = NaiveBayesClassifier(train)

target_names = ['Class 1', 'Class 2','Class 3']
print("Naive Bayes")
predicted=[]
rumor_count=0
ad_count=0
normal_count=len(test)
for item in test:
    labels = int(clf.classify(item))
    predicted.append(clf.classify(item))
    if labels == 1:
        rumor_count += 1
    elif labels == 2:
        ad_count += 1
    print('%s => %s' % (item, ''.join(target_names[labels])))
    
print("accuracy score : ",accuracy_score(Real_Value, predicted))
print('루머성 확률 : ', (rumor_count/normal_count)*100,'%' )
print('광고성 확률 : ', (ad_count/normal_count)*100,'%' )
