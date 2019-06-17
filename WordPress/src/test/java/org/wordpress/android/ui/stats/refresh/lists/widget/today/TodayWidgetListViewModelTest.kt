package org.wordpress.android.ui.stats.refresh.lists.widget.today

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.model.stats.VisitsModel
import org.wordpress.android.fluxc.store.SiteStore
import org.wordpress.android.fluxc.store.stats.insights.TodayInsightsStore
import org.wordpress.android.ui.stats.refresh.lists.widget.configuration.StatsWidgetConfigureViewModel.Color
import org.wordpress.android.ui.stats.refresh.lists.widget.today.TodayWidgetListViewModel.TodayItemUiModel
import org.wordpress.android.viewmodel.ResourceProvider

@RunWith(MockitoJUnitRunner::class)
class TodayWidgetListViewModelTest {
    @Mock private lateinit var siteStore: SiteStore
    @Mock private lateinit var store: TodayInsightsStore
    @Mock private lateinit var resourceProvider: ResourceProvider
    @Mock private lateinit var site: SiteModel
    private lateinit var viewModel: TodayWidgetListViewModel
    private val siteId: Int = 15
    private val appWidgetId: Int = 1
    private val color = Color.LIGHT
    @Before
    fun setUp() {
        viewModel = TodayWidgetListViewModel(siteStore, store, resourceProvider)
        viewModel.start(siteId, color, appWidgetId)
    }

    @Test
    fun `builds ui model`() {
        whenever(siteStore.getSiteByLocalId(siteId)).thenReturn(site)
        val viewsKey = "Views"
        val visitorsKey = "Visitors"
        val postsKey = "Posts"
        val commentsKey = "Comments"
        val views = 500
        val visitors = 100
        val posts = 50
        val comments = 300
        whenever(resourceProvider.getString(R.string.stats_views)).thenReturn(viewsKey)
        whenever(resourceProvider.getString(R.string.stats_visitors)).thenReturn(visitorsKey)
        whenever(resourceProvider.getString(R.string.posts)).thenReturn(postsKey)
        whenever(resourceProvider.getString(R.string.stats_comments)).thenReturn(commentsKey)
        whenever(store.getTodayInsights(site)).thenReturn(
                VisitsModel("2019-10-10", views, visitors, 0, 0, comments, posts)
        )

        viewModel.onDataSetChanged { }

        assertThat(viewModel.data).hasSize(4)
        assertListItem(viewModel.data[0], viewsKey, views)
        assertListItem(viewModel.data[1], visitorsKey, visitors)
        assertListItem(viewModel.data[2], postsKey, posts)
        assertListItem(viewModel.data[3], commentsKey, comments)
    }

    @Test
    fun `shows error when site is missing`() {
        whenever(siteStore.getSiteByLocalId(siteId)).thenReturn(null)

        var onError: Int? = null
        viewModel.onDataSetChanged { onError = it }

        assertThat(onError).isEqualTo(appWidgetId)
    }

    private fun assertListItem(listItem: TodayItemUiModel, key: String, value: Int) {
        assertThat(listItem.layout).isEqualTo(R.layout.stats_views_widget_item_light)
        assertThat(listItem.localSiteId).isEqualTo(siteId)
        assertThat(listItem.key).isEqualTo(key)
        assertThat(listItem.value).isEqualTo(value.toString())
    }
}
