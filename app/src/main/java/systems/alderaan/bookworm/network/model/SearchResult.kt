package systems.alderaan.bookworm.network.model

@kotlinx.serialization.Serializable
data class SearchResult(
    val totalItems: Int,
    val items: List<SearchResultItem> = listOf()
)
