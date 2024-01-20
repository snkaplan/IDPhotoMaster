package com.idphoto.idphotomaster.core.common.extension

import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup

fun GPUImage.applyFilters(filter: GPUImageFilterGroup) {
    this.setFilter(filter)
}