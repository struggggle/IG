Ammx--money manager

总共要测试18个app。
而且都是非常简单的功能性测试用例。


关于测试用例的说明：

PhotoManager:   3个test cases

de.k3b.android.androFotoFinder
de.k3b.android.androFotoFinder/.FotoGalleryActivity

WordPress:大概10个test cases


这里主要是覆盖主要功能，同时基于主要功能的基础上，增加一些滑动等操作，提供一些变体，例如增加滑动，增加重复。

这样，既包含了简单的，又包含了复杂的。

createBlog：包含两次create blog，一个是在主界面点击编辑按钮，一个是在blog list点击编辑按钮。两者的区别在于一个添加了四张图片，一个仅仅添加了2张图片。

openMedia:里面点击图片没有带来额外的图片显示，只有滑动的时候有显示

openThemes:点开之后的图片是在webView中,并且出现了是否接受隐私的框框；

edit+addImage:首先对image进行edit命名，然后添加图片，如果不编辑直接添加图片的button和添加图片之后的button是不同的位置。同时，如果编辑的步骤没有完成就结束，会导致界面发生变化，导致某个event失效。
而且这个测试用例可以变得更加的复杂，还没有进行图片的滑动。

更加复杂的测试用例：

将多个代码components串联起来，虽然多个components是不同的功能，但是它们会显示相同的image，讲道理它们应该共用cache的image才行。









