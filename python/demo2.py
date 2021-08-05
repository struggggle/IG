import sys
import re

from lxml import etree

from hashlib import md5 as MD5

from collections import namedtuple
from typing import Dict, Tuple

#from anip.curses.win import wprint
#from anip.state import *
#from utils.hash import md5
#from utils.cache import LruCache
#from utils.lxml import sanitize_head
#from utils.uiautomator import Widget

def func(a,b):
    xml1=etree.parse(a)
    xml2=etree.parse(b)

    root1=xml1.getroot()
    root2=xml2.getroot()

    #print(root1.items())
    #print(root2.items())


    eq = StateEqOperator()
    value = eq._eq_appstate_node(root1, root2, ignore_bounds=False) #start point
    print(value)#这个返回值非常的重要，java程序直接根据这个返回值判断python的执行结果
    return root1.items() #测试返回值


#below is copied from cache.py
CacheInfo = namedtuple('CacheInfo', ['size', 'hits', 'misses', 'max_size', 'sentinel'])


class _CacheNode:

    def __init__(self, key_, value_, next_=None, prev_=None):
        self.key = key_
        self.value = value_
        self.prev = self if prev_ is None else prev_
        self.next = self if next_ is None else next_


class LruCache:

    def __init__(self, sentinel, max_size=128):
        self._leader_node = _CacheNode(key_=None, value_=None)
        self._sentinel_node = _CacheNode(key_=None, value_=sentinel,
                                         prev_=None, next_=None)
        self._node_dict: Dict[Tuple[str, str], _CacheNode] = {}
        self._node_list = self._leader_node
        self._hits = 0
        self._misses = 0
        self._max_size = max_size
        self._sentinel = sentinel

    @property
    def info(self):
        return CacheInfo(size=self.size, hits=self.hits, misses=self.misses,
                         max_size=self.max_size, sentinel=self.sentinel)

    @property
    def size(self):
        return len(self._node_dict)

    @property
    def max_size(self):
        return self._max_size

    @property
    def hits(self):
        return self._hits

    @property
    def misses(self):
        return self._misses

    @property
    def sentinel(self):
        return self._sentinel

    def get(self, key):
        try:
            node = self._node_dict[key]
            self._hits += 1
        except KeyError:
            node = self._sentinel_node
            self._misses += 1
        return node.value

    def put(self, key, value):
        if key in self._node_dict:
            node = self._node_dict[key].value
            node.value = value
            node.prev.next = node.next
            node.next.prev = node.prev
            node.prev = node.next = node
        else:
            node = _CacheNode(key_=key, value_=value)
        if self.size > self.max_size:
            self._rm_oldest()
        self._add_youngest(node)

    def _add_youngest(self, node):
        node.next = self._leader_node
        node.prev = self._leader_node.prev
        node.next.prev = node.prev.next = node
        self._node_dict[node.key] = node

    def _rm_oldest(self):
        oldest_node = self._leader_node.next
        self._leader_node.next = oldest_node.next
        oldest_node.next.prev = self._leader_node
        oldest_node.prev = oldest_node.next = None
        del self._node_dict[oldest_node.key]


class StateEqOperator():

    # the cache
    _cache = LruCache(sentinel=object())

    def __init__(self, data_sensitive=False,
                 tolerant_percentage=0.25,
                 max_diff_aps=3,
                 min_joint_aps_percentage=0.75):
        # data sensitive is for data loss checking
        self.data_sensitive = data_sensitive
        # tolerant some element difference, if their area take-up percentage are small
        self.tolerant_percentage = tolerant_percentage
        # our guarantee is that, same ones must be equal, and totally different ones
        # cannot be equal. so potentially semantically equal nodes, must have less than
        # max_diff_aps different attribute paths, and more than min_joint_aps_percentage
        # same attribute paths
        self.max_diff_aps = max_diff_aps
        self.min_joint_aps_percentage = min_joint_aps_percentage

    def eq_appstate(self, root1, root2) -> bool:
        # if s1.ui_content is None:
        #     s1.ui_content = AnipAdb.get().read_dump(s1.ui_path)

        # if s2.ui_content is None:
        #     s2.ui_content = AnipAdb.get().read_dump(s2.ui_path)

        # s1.ui_content = sanitize_head(s1.ui_content)
        # s2.ui_content = sanitize_head(s2.ui_content)

        # s1_hash, s2_hash = self.md5(s1.ui_content), self.md5(s2.ui_content)
        # value = self._cache.get((s1_hash, s2_hash))
        # if value != self._cache.sentinel:
        #     return value
        # elif s1_hash == s2_hash:  # simply treat eq as eq of hash
        #     self._cache.put((s1_hash, s2_hash), value=True)
        #     return True

        # try:
        #     root1 = etree.fromstring(s1.ui_content)
        # except ValueError as e:
        #     #wprint(s1.ui_content)
        #     raise e

        # try:
        #     root2 = etree.fromstring(s2.ui_content)
        # except ValueError as e:
        #     #wprint(s2.ui_content)
        #     raise e

        value = self._eq_appstate_node(root1, root2, ignore_bounds=False) #start point
        self._cache.put((s1_hash, s2_hash), value=value)

        return value

    def _eq_appstate_node(self, n1, n2, ignore_bounds):
        if n1.tag != n2.tag:
            return False

        if n1.tag != 'hierarchy':
            n1_package = n1.get('package')
            n2_package = n2.get('package')
            n1_class = n1.get('class')
            n2_class = n2.get('class')

            # They have to be the same widget
            if n1_package != n2_package or n1_class != n2_class:
                return False

            # Data sensitive checking, for input widgets
            if self.data_sensitive:
                data_sensitive_view_tab = {
                    'android.widget.EditText':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'androidx.appcompat.widget.AppCompatEditText':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'android.widget.AutoCompleteTextView':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'androidx.appcompat.widget.AppCompatAutoCompleteTextView':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'android.widget.MultiAutoCompleteTextView':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'android.widget.SearchView':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'androidx.appcompat.widget.SearchView':
                        lambda x, y: self._eq_appstate_edit_text(x, y),
                    'android.widget.CheckBox':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'androidx.appcompat.widget.AppCompatCheckBox':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'android.widget.CheckedTextView':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'androidx.appcompat.widget.AppCompatCheckedTextView':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'android.widget.RadioButton':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'androidx.appcompat.widget.AppCompatRadioButton':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'android.widget.ToggleButton':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'androidx.appcompat.widget.AppCompatToggleButton':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'android.widget.Switch':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                    'androidx.appcompat.widget.SwitchCompat':
                        lambda x, y: self._eq_appstate_compound_button(x, y),
                }

                if n1_class in data_sensitive_view_tab:
                    return data_sensitive_view_tab[n1_class](n1, n2)

            # Specialization
            special_view_tab = {
                'android.support.v7.app.ActionBar$Tab':
                    lambda x, y: self._eq_appstate_action_bar_tab(x, y),
                'android.widget.ListView':
                    lambda x, y: self._eq_appstate_list(x, y),
                'android.support.v7.widget.RecyclerView':
                    lambda x, y: self._eq_appstate_list(x, y),
                'android.support.v17.leanback.widget.HorizontalGridView':
                    lambda x, y: self._eq_appstate_list(x, y),
                'android.support.v17.leanback.widget.VerticalGridView':
                    lambda x, y: self._eq_appstate_list(x, y),
                'android.widget.GridView':
                    lambda x, y: self._eq_appstate_list(x, y),
                'android.webkit.WebView':
                    lambda x, y: self._eq_appstate_webview(x, y),
            }

            if n1_class in special_view_tab:
                return special_view_tab[n1_class](n1, n2)

        # Assume: each dump of uiautomator is the same for the same screen
        n1_children = n1.getchildren()
        n2_children = n2.getchildren()

        n1_num_children = len(n1_children)
        n2_num_children = len(n2_children)

        # Both are leaf nodes
        if n1_num_children == 0 and n2_num_children == 0:
            return self._eq_appstate_node_leaf(n1, n2)
        # Both are non-leaf nodes
        elif n1_num_children != 0 and n2_num_children != 0:
            return self._eq_appstate_node_nonleaf(n1, n2, ignore_bounds=ignore_bounds)
        elif n1_num_children == 0:
            return self._eq_appstate_node_leaf_nonleaf(n2, n1)
        else:
            return self._eq_appstate_node_leaf_nonleaf(n1, n2)

    def _eq_appstate_edit_text(self, e1, e2):
        # in data-sensitive mode, text field of EditText should reserve
        # and as usual, we treat EditText as a leaf node
        return (e1.get('text') == e2.get('text')) and self._eq_appstate_node_leaf(e1, e2)

    def _eq_appstate_compound_button(self, e1, e2):
        # in data-sensitive mode, checked field of CompoundButton, e.g., CheckBox,
        # RadioButton should reserve, and as usual, we treat CompoundButton as a
        # leaf node
        return (e1.get('checked') == e2.get('checked')) and self._eq_appstate_node_leaf(e1, e2)

    def _eq_appstate_list(self, n1, n2):
        # Two list are eq as long as their children are in the same layout
        # So for list, we directly return the eq(n1.first_child, n2.first_child)

        # check bounds
        if not self._check_bounds(n1, n2):
            return False

        n1_children = n1.getchildren()
        n2_children = n2.getchildren()

        n1_num_children = len(n1_children)
        n2_num_children = len(n2_children)

        # an empty list vs a non-empty list, we conservatively treat them as eq,
        # because
        #   (1) if this two states are different, it POSSIBLY be recognized by
        #   other widgets other than this list.
        #   (2) if this two states are eq, then True is apparently correct
        if n1_num_children == 0 or n2_num_children == 0:
            return True

        # Some lists often include a header elements, skip it
        if n1_num_children > 1 and n2_num_children > 1:
            return self._eq_appstate_node(n1_children[1], n2_children[1], ignore_bounds=True)

        return self._eq_appstate_node(n1_children[0], n2_children[0], ignore_bounds=True)

    def _eq_appstate_action_bar_tab(self, t1, t2):
        # Tabs are often used as indicator of some different pages, or fragment,
        # they have to remain exactly the same
        return self._eq_appstate_exactly(t1, t2)

    def _eq_appstate_webview(self, _, __):
        # TODO uiautomator sometimes is able to dump webview, but sometimes isn't.
        #   Temporarily treat WebView as a special case. Remove WebView the time
        #   figured out when and why uiautomator behaves like this.
        return True

    def _eq_appstate_exactly(self, n1, n2):
        # Attributes, including packages, and classes, have to be exactly the same
        n1_keys = n1.keys()
        n2_keys = n2.keys()

        if len(n1_keys) != len(n2_keys):
            return False

        for key in n1_keys:
            if key == 'focused':
                continue
            elif key not in n2_keys:
                return False
            elif n1.get(key) != n2.get(key):
                return False

        # Children have to be exactly the same
        n1_children = n1.getchildren()
        n2_children = n2.getchildren()

        n1_num_children = len(n1_children)
        n2_num_children = len(n2_children)

        if n1_num_children == n2_num_children:
            ret = True
            for i in range(n1_num_children):
                ret = ret and self._eq_appstate_exactly(n1_children[i], n2_children[i])
        else:
            ret = False

        return ret

    def _eq_appstate_node_leaf_nonleaf(self, leaf, nonleaf):
        # TODO: are they potentially to be equal??? Maybe some brands or notifications?
        return False

    def _eq_appstate_node_nonleaf(self, n1, n2, ignore_bounds):
        # For layout, they have to start from the same bounds,
        # however, bounds can be ignored, e.g., bounds of list children
        if not ignore_bounds:
            if not self._check_bounds(n1, n2):
                return False

        n1_children = n1.getchildren()
        n2_children = n2.getchildren()

        n1_num_children = len(n1_children)
        n2_num_children = len(n2_children)

        # Attribute paths [(index, class)...]
        n1_children_ap = set([(c.get('index'), c.get('class')) for c in n1_children])
        n2_children_ap = set([(c.get('index'), c.get('class')) for c in n2_children])

        # Our guarantee is that, same ones must be equal, and totally different ones
        # cannot be equal. so potentially semantically equal nodes, must have less than
        # max_diff_aps different attribute paths, and more than min_joint_aps_percentage
        # same attribute paths
        less_num = n1_num_children if n1_num_children < n2_num_children else n2_num_children
        joint_ap = n1_children_ap.intersection(n2_children_ap)
        num_joint_ap = len(joint_ap)
        num_diff_ap = less_num - num_joint_ap
        if (num_joint_ap / less_num) < self.min_joint_aps_percentage or \
                num_diff_ap > self.max_diff_aps:
            return False

        # Nodes compared have enough more joint attributes paths, and enough less
        # different ones, they are potentially semantically equal

        # Check same attribute paths using the same strategy
        joint_fn = lambda c: (c.get('index'), c.get('class')) in joint_ap
        n1_joint = filter(joint_fn, n1_children)
        n2_joint = filter(joint_fn, n2_children)
        for c1, c2 in zip(n1_joint, n2_joint):
            if not self._eq_appstate_node(c1, c2, ignore_bounds=ignore_bounds):
                return False

        # Area taken up by different attribute paths cannot exceed self.tolerant_percentage
        diff_fn = lambda c: not joint_fn(c)
        n1_diff = filter(diff_fn, n1_children)
        n2_diff = filter(diff_fn, n2_children)
        for c in n1_diff:
            percentage = self._cal_area_percent(n1, c)
            if percentage > self.tolerant_percentage:
                return False
        for c in n2_diff:
            percentage = self._cal_area_percent(n1, c)
            if percentage > self.tolerant_percentage:
                return False

        return True

    def _eq_appstate_node_leaf(self, l1, l2):
        # For leaf nodes, they have to be the same on the same property
        # The full list is:
        #   'package', 'class', 'resource-id',
        #   'index', 'content-desc',
        #   'checkable', 'enabled','focusable', 'scrollable',
        #   'long-clickable', 'password', 'selected'
        properties = ['checkable', 'enabled', 'focusable', 'scrollable',
                      'long-clickable', 'password', 'selected']

        ret = True
        for k in properties:
            ret = ret and (l1.get(k) == l2.get(k))

        return ret

    def _check_bounds(self, n1, n2):
        n1_bounds = n1.get('bounds')
        n2_bounds = n2.get('bounds')

        if n1_bounds is not None and n2_bounds is not None:
            n1_bounds_top_left = n1_bounds[:n1_bounds.rfind('[')]
            n2_bounds_top_left = n2_bounds[:n2_bounds.rfind('[')]
            if n1_bounds_top_left != n2_bounds_top_left:
                return False

        return True

    def _cal_area_percent(self, parent, child):
        parent_bounds = parent.get('bounds')
        child_bounds = child.get('bounds')

        if parent_bounds is None and child_bounds is None:
            return 1
        elif child_bounds is None:
            return 0
        elif parent_bounds is not None:
            (pl, pt), (pr, pb) = self.get_bounds(parent_bounds)
            (cl, ct), (cr, cb) = self.get_bounds(child_bounds)
            return ((int(cb) - int(ct)) * (int(cr) - int(cl))) / \
                   ((int(pb) - int(pt)) * (int(pr) - int(pl)))
        else:
            assert 0

    # def __call__(self, s1: State, s2: State) -> bool:
    #     # StubState equals to any type of state
    #     if isinstance(s1, StubState) or isinstance(s2, StubState):
    #         return True
    #     # Only the same state can be compared
    #     elif type(s1) != type(s2):
    #         return False
    #     elif isinstance(s1, AppState) and isinstance(s2, AppState):
    #         return self.eq_appstate(s1, s2)
    #     else:
    #         assert 0

    #below is added from other .py files
    def md5(s: str):
        m = MD5()
        m.update(s.encode())
        return m.hexdigest()

    def sanitize_head(content):
        head = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"
        # lxml cannot parse any xml with unicode encoding
        if content.startswith(head):
          content = content[len(head):]
        return content

    def get_bounds(bounds):
        if bounds is None:
            return (-1, -1), (-1, -1)

        regex = re.compile(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]')
        l, t, r, b = regex.findall(bounds)[0]

        return (int(l), int(t)), (int(r), int(b))


if __name__ == '__main__':
    # print(sys.argv[1])
    # print(sys.argv[2])
    func(sys.argv[1],sys.argv[2])

    #test
    #a='$directoryPath/xml/layout33.xml'
    #b="$directoryPath/xml/layout33.xml"
    #func(a,b)

