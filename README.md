# ImagePicker-master
图片选择器，包含拍照、单选/多选、旋转等功能

## Dependency
- Add the library to your module `build.gradle`
```gradle
dependencies {
	implementation 'com.stevexls.widget:imagepicker:1.0.0'
}
```
## Usage
- Step 1.创建自己的图片加载器，需要实现com.stevexls.imagepicker.engine接口
```java
public class GlideEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, ImageView imageView, String path) {
        Glide.with(context)
                .asDrawable()
                .load(path)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter())
                .into(imageView);
    }

    ...
    ...
}
```

- Step 2.使用
```java
ImagePicker.from(this)
            .choose(MimeType.ofAll(), false)
            .showSingleMediaType(false)
            .countable(true)
            .theme(R.style.Custom_Theme)     // R.style.ImagePicker.Theme
            .multiMode(true)
            .maxSelectable(8)
            .imageEngine(new GlideEngine())
            .captureStrategy(new CaptureStrategy(true, "com.stevexls.imagepickerdemo.fileprovider", "ImagePicker/Pictures"))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .capture(true)
//            .thumbnailScale(1.0f)
            .spanCount(4)
            .statucBarDarkMode(true)
            .originalEnable(true)
            .showSelected(true)
            .selectedItems(selectItems)
            .forResult(REQUEST_CODE_CHOOSE);
```

- Step 3.回调结果
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK || data == null) {
        return;
    }
    if(requestCode == REQUEST_CODE_CHOOSE){
        ArrayList<Item> mLists = data.getParcelableArrayListExtra(EXTRA_RESULT_SELECTION);
    }
}
```

## Parameters
|配置参数|参数含义|
|:--:|--|
|mimeTypeSet|查询类型，MimeType.ofAll()：查所有；MimeType.ofImage()：查图片；MimeType.ofVideo()：查视频|
|mediaTypeExclusive|是否可以同时选择图片和视频 false：允许同时选择;true：不允许|
|showSingleMediaType|显示单类型文件|
|themeId|主题，可自定义|
|orientation|屏幕方向|
|countable|是否计数模式|
|maxSelectable|最大选择数，默认9|
|capture|是否拍照|
|captureStrategy|拍照设定|
|spanCount|列数|
|imageEngine|图片加载|
|multiMode|图片选择模式，true:多选; false:单选|
|showSelected|是否显示已选中图片，true:显示; false:不显示|
|selectedItems|已选中item list|
|statusBarDarkMode|状态栏字体颜色 true:黑色; false:白色|

## Thanks
- https://github.com/zhihu/Matisse
- https://github.com/jeasonlzy/ImagePicker

## License

    Copyright 2019 Stevexls. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
