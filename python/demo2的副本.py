import sys
from lxml import etree

def func(a,b):
	xml1=etree.parse(a)
	xml2=etree.parse(b)

	root1=xml1.getroot()
	root2=xml2.getroot()

	print(root1.items())
	print(root2.items())
    


 
if __name__ == '__main__':
	print(sys.argv[1])
	print(sys.argv[2])
	a='$directoryPath/xml/layout33.xml'
	b="$directoryPath/xml/layout33.xml"
	func(sys.argv[1],sys.argv[2])
