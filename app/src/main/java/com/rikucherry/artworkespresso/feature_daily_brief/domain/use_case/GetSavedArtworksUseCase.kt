package com.rikucherry.artworkespresso.feature_daily_brief.domain.use_case

import com.rikucherry.artworkespresso.common.tool.DataFormatHelper.isOneWeekAgo
import com.rikucherry.artworkespresso.common.tool.LocalResource
import com.rikucherry.artworkespresso.feature_daily_brief.data.local.data_source.SavedArtworkItem
import com.rikucherry.artworkespresso.feature_daily_brief.data.repository.DailyBriefRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSavedArtworksUseCase @Inject constructor(
    private val dailyBriefRepository: DailyBriefRepository
) {
    operator fun invoke(weekday: String, isFreeTrail: Boolean)
            : Flow<LocalResource<List<SavedArtworkItem>?>> = flow {
        emit(LocalResource.Loading("Requesting saved artworks information..."))

        try {
            val savedArtworkItems
            = dailyBriefRepository.getArtworksByWeekday(weekday, isFreeTrail)?.toMutableList()

            if (!savedArtworkItems.isNullOrEmpty()) {
                val oldItems = savedArtworkItems.filter {
                    item ->
                    isOneWeekAgo(item.savedTime)
                }

                if (oldItems.isNotEmpty()) {
                    dailyBriefRepository.deleteSavedArtworksByWeekday(weekday, isFreeTrail)
                    savedArtworkItems.clear()
                }
            }

            emit(LocalResource.Success(savedArtworkItems))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(LocalResource.Exception(e.localizedMessage ?: ""))
        }
    }
}