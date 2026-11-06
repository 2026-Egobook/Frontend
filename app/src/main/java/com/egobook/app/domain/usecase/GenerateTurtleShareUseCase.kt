package com.egobook.app.domain.usecase

import com.egobook.app.data.share.TurtleShareRepository
import com.egobook.app.store.ui.CustomItem
import javax.inject.Inject

class GenerateTurtleShareUseCase @Inject constructor(private val repository: TurtleShareRepository) {
    suspend operator fun invoke(items: List<CustomItem>) = repository.generate(items)
}
