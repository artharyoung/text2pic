# text2pic
简单的文字转图片

# usage
- sample
```kotlin
val file = File("D:\\temp.png")
val textCanvas = TextCanvas()
textCanvas.addString("别了，司徒雷登\n\n（一九四九年八月十四日）\n")
textCanvas.writeTo(file)
```
![](\temp.png)