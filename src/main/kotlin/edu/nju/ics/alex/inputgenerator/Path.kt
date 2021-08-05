package edu.nju.ics.alex.inputgenerator


class PathNode {
    public var name: String?=null
    public var nodePath: List<Path>?=null
    public var children = mutableListOf<PathNode>()
    public var parent: PathNode?=null
    public var bounds =IntArray(4)//包含了一个widget的域
    public var clickable: String?=null
    public var scrollable: String?=null
    public var checkable:String?=null
    public var clazz:String?=null
    public var index:String?=null

}

class Path {
    public var id: String? = null
    public var clazz: String? = null
}