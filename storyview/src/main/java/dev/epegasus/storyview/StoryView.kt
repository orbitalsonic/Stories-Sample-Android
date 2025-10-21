package dev.epegasus.storyview

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import dev.epegasus.storyview.adapters.CustomViewPagerAdapter
import dev.epegasus.storyview.dataClasses.HeaderInfo
import dev.epegasus.storyview.dataClasses.MyStory
import dev.epegasus.storyview.databinding.DialogStoriesBinding
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener
import dev.epegasus.storyview.listeners.OnTouchCallback
import dev.epegasus.storyview.listeners.StoriesListener
import dev.epegasus.storyview.listeners.StoryCallback
import dev.epegasus.storyview.listeners.pull_dismiss_listener.OnPullDismissListener
import dev.epegasus.storyview.utils.DateUtils.getDurationBetweenDates
import dev.epegasus.storyview.utils.GeneralUtils.getActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

class StoryView : Fragment(), StoriesListener, StoryCallback, OnPullDismissListener, OnTouchCallback {

    private var _binding: DialogStoriesBinding? = null
    private val binding get() = _binding!!

    private val globalActivity by lazy { binding.root.context.getActivity() }

    // Setting Views
    private var counter = 0

    // Functions
    private var onStoryClickListener: OnStoryClickListener? = null
    private var onStoryChangeListener: OnStoryChangeListener? = null
    
    // Flag to track if download button is being touched
    private var isDownloadButtonTouched = false
    
    // Touch coordinates to check if touch is in download button area
    private var downloadButtonTouchX = 0f
    private var downloadButtonTouchY = 0f

    // Coroutine Jobs
    private var pauseJob: Job? = null

    // Bundles
    private var _storyList = ArrayList<MyStory>()
    private val storyList: List<MyStory> get() = _storyList.toList()
    private var startingIndex = 0
    private var duration: Long = 2000 //Default Duration
    private var isRtl = false

    private var isHeadlessLogoMode = false
    private var didPause = false

    //Touch Events
    private var width = 0
    private var height = 0
    private var xValue = 0f
    private var yValue = 0f
    private var downX = 0f
    private var downY = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogStoriesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDisplay()
        readArguments()
        setupViews()
        setupStories()
    }

    private fun setDisplay() {
        val resources = context?.resources
        if (resources != null) {
            val displayMetrics = resources.displayMetrics
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        } else {
            // Fallback to default values if context is null
            width = 1080
            height = 1920
        }
    }

    @Suppress("DEPRECATION")
    private fun readArguments() {
        arguments?.let { bundle ->
            val temp: ArrayList<MyStory>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle.getParcelableArrayList(IMAGES_KEY, MyStory::class.java)
            else
                bundle.getParcelableArrayList(IMAGES_KEY)
            temp?.let { it -> _storyList.addAll(it) }

            startingIndex = bundle.getInt(STARTING_INDEX_TAG, 0)
            duration = bundle.getLong(DURATION_KEY, 2000)
            isRtl = bundle.getBoolean(IS_RTL_TAG, false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        binding.pullDismissLayout.setListener(this)
        binding.pullDismissLayout.setTouchCallbacks(this)

        binding.storiesProgressView.setStoriesListener(this)
        if (isRtl) {
            binding.storiesProgressView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            binding.storiesProgressView.rotation = 180f
        }

        binding.ifvCloseDialogStories.setOnClickListener { onDismissed() }
        binding.ifvImageDialogStories.setOnClickListener { onStoryClickListener?.onTitleIconClickListener(counter) }

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.setOnTouchListener { _, _ -> true }

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                onStoryChangeListener?.storyChanged(position)
            }
        })
    }

    private fun setupStories() {
        if (storyList.isEmpty()) {
            Log.w(TAG, "Story list is empty, dismissing story view")
            onDismissed()
            return
        }

        binding.storiesProgressView.setStoriesCount(storyList.size)
        binding.storiesProgressView.setStoryDuration(duration)
        updateHeading()
        val adapter = CustomViewPagerAdapter(storyList, this) { position, imageUrl ->
            onStoryClickListener?.onDownloadClickListener(position, imageUrl)
        }
        binding.viewPager.adapter = adapter
    }

    /* -------------------------------------- Functions -------------------------------------- */

    fun setStoryClickListeners(storyClickListeners: OnStoryClickListener?) {
        this.onStoryClickListener = storyClickListeners
    }

    fun setOnStoryChangedCallback(onStoryChangeListener: OnStoryChangeListener?) {
        this.onStoryChangeListener = onStoryChangeListener
    }
    
    /**
     * Set flag to indicate download button is being touched
     * This prevents story swipe from being triggered
     */
    override fun setDownloadButtonTouched(touched: Boolean) {
        isDownloadButtonTouched = touched
    }
    
    /**
     * Check if touch coordinates are in the download button area
     * Download button is positioned in bottom-right corner
     */
    private fun isTouchInDownloadButtonArea(x: Float, y: Float): Boolean {
        if (_binding == null) return false
        
        // Download button is in bottom-right corner
        // Approximate area: right 20% of width, bottom 20% of height
        val rightMargin = width * 0.2f
        val bottomMargin = height * 0.2f
        
        val isInRightArea = x >= (width - rightMargin)
        val isInBottomArea = y >= (height - bottomMargin)
        
        Log.d(TAG, "StoryView: isTouchInDownloadButtonArea: x=$x, y=$y, width=$width, height=$height, rightMargin=$rightMargin, bottomMargin=$bottomMargin, isInRightArea=$isInRightArea, isInBottomArea=$isInBottomArea")
        
        return isInRightArea && isInBottomArea
    }

    /* -------------------------- Stories Listener -------------------------- */

    override fun onNext() {
        if (_binding == null) return
        if (counter + 1 < storyList.size) {
            binding.viewPager.setCurrentItem(++counter, false)
            updateHeading()
        }
    }

    override fun onPrev() {
        if (_binding == null) return
        if (counter <= 0) return
        binding.viewPager.setCurrentItem(--counter, false)
        updateHeading()
    }

    override fun onComplete() {
        onDismissed()
    }

    /* -------------------------- StoryCallback -------------------------- */

    override fun startStories() {
        // Check if binding is available (fragment view might be destroyed)
        if (_binding == null) {
            Log.w(TAG, "Cannot start stories: binding is null (fragment view destroyed)")
            return
        }
        
        if (startingIndex < 0 || startingIndex >= storyList.size) {
            Log.w(TAG, "Invalid starting index: $startingIndex, using 0")
            startingIndex = 0
        }
        counter = startingIndex
        binding.storiesProgressView.resetAllProgress() // Reset all progress bars first
        binding.storiesProgressView.startStories(startingIndex)
        binding.viewPager.setCurrentItem(startingIndex, false)
        updateHeading()
    }

    override fun pauseStories() {
        if (_binding == null) return
        binding.storiesProgressView.pause()
    }
    
    /**
     * Restart stories from the beginning
     */
    fun restartStories() {
        if (_binding == null) {
            Log.w(TAG, "Cannot restart stories: binding is null (fragment view destroyed)")
            return
        }
        
        Log.d(TAG, "Restarting stories from beginning")
        counter = 0
        startingIndex = 0
        binding.storiesProgressView.resetAllProgress()
        binding.storiesProgressView.startStories(0)
        binding.viewPager.setCurrentItem(0, false)
        updateHeading()
    }

    private fun previousStory() {
        if (counter <= 0) {
            // Already at first story
            if (binding.storiesProgressView.isPaused()) {
                binding.storiesProgressView.resume()
            }
            return
        }

        binding.viewPager.setCurrentItem(--counter, false)
        binding.storiesProgressView.setStoriesCount(storyList.size)
        binding.storiesProgressView.setStoryDuration(duration)
        binding.storiesProgressView.startStories(counter)
        updateHeading()
    }

    override fun nextStory() {
        if (counter + 1 >= storyList.size) {
            onDismissed()
            return
        }
        binding.viewPager.setCurrentItem(++counter, false)
        binding.storiesProgressView.startStories(counter)
        updateHeading()
    }

    override fun onDescriptionClickListener(position: Int) {
        if (onStoryClickListener == null) return
        onStoryClickListener!!.onDescriptionClickListener(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        pauseJob?.cancel()
        _storyList.clear()
        _binding?.storiesProgressView?.destroy()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    private fun updateHeading() {
        if (_binding == null) return
        
        val temp: ArrayList<HeaderInfo>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelableArrayList(HEADER_INFO_KEY, HeaderInfo::class.java)
        else
            arguments?.getParcelableArrayList(HEADER_INFO_KEY)

        var headerInfo: HeaderInfo? = null

        temp?.let { list ->
            if (list.isEmpty()) {
                Log.w(TAG, "Header info list is empty")
                return
            }

            if (list.size == 1) {
                headerInfo = list[0]
            } else if (counter >= 0 && counter < list.size) {
                headerInfo = list[counter]
            }
        }

        headerInfo?.titleIconUrl?.let { url ->
            binding.ifvImageDialogStories.visibility = View.VISIBLE
            context?.let {
                Glide.with(it).load(url).into(binding.ifvImageDialogStories)
            }
        } ?: kotlin.run {
            binding.ifvImageDialogStories.visibility = View.INVISIBLE
            isHeadlessLogoMode = true
        }

        headerInfo?.title?.let {
            binding.mtvTitleDialogStories.visibility = View.VISIBLE
            binding.mtvTitleDialogStories.text = it
        } ?: kotlin.run {
            binding.mtvTitleDialogStories.visibility = View.GONE
        }

        headerInfo?.subtitle?.let {
            binding.mtvSubtitleDialogStories.visibility = View.VISIBLE
            binding.mtvSubtitleDialogStories.text = it
        } ?: kotlin.run {
            binding.mtvSubtitleDialogStories.visibility = View.GONE
        }

        if (counter >= 0 && counter < storyList.size) {
            storyList[counter].date?.let {
                val text = "${binding.mtvTitleDialogStories.text} ${getDurationBetweenDates(it, Calendar.getInstance().time)}"
                binding.mtvTitleDialogStories.text = text
            }
        }
    }

    private fun setHeadingVisibility(visibility: Int) {
        if (isHeadlessLogoMode && visibility == View.VISIBLE) {
            binding.ifvImageDialogStories.visibility = View.INVISIBLE
            binding.mtvTitleDialogStories.visibility = View.GONE
            binding.mtvSubtitleDialogStories.visibility = View.GONE
        } else {
            binding.ifvImageDialogStories.visibility = visibility
            binding.mtvTitleDialogStories.visibility = visibility
            binding.mtvSubtitleDialogStories.visibility = visibility
        }
        binding.ifvCloseDialogStories.visibility = visibility
        binding.storiesProgressView.visibility = visibility
    }

    /* -------------------------- Pull Dismiss Listener -------------------------- */

    override fun onDismissed() {
        parentFragmentManager.popBackStack()
        onStoryChangeListener?.storyDismiss()
    }

    override fun onShouldInterceptTouchEvent(): Boolean {
        return false
    }

    /* -------------------------- Touch Callback -------------------------- */

    override fun touchHorizontalSwipe(swipeDirection: Int) {
        // Don't trigger story swipe if download button was touched
        if (isDownloadButtonTouched) {
            Log.d(TAG, "StoryView: touchHorizontalSwipe: Ignoring swipe - download button was touched")
            isDownloadButtonTouched = false // Reset flag
            return
        }
        onStoryChangeListener?.storySwiped(swipeDirection)
    }

    override fun touchPull() {
        pauseJob?.cancel()
        binding.storiesProgressView.pause()
    }

    override fun touchDown(xValue: Float, yValue: Float) {
        this.xValue = xValue
        this.yValue = yValue
        downX = xValue
        downY = yValue
        didPause = false

        // Check if touch is in download button area (bottom-right corner)
        val isInDownloadArea = isTouchInDownloadButtonArea(xValue, yValue)
        if (isInDownloadArea) {
            Log.d(TAG, "StoryView: touchDown: Touch in download button area - ignoring")
            isDownloadButtonTouched = true
            return
        }

        pauseJob?.cancel()
        pauseJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(35) // <- threshold for "long press" in ms
            binding.storiesProgressView.pause()
            didPause = true
            // optionally: setHeadingVisibility(View.GONE)
        }
    }

    override fun touchUp() {
        pauseJob?.cancel()

        // Check if touch was in download button area
        if (isDownloadButtonTouched) {
            Log.d(TAG, "StoryView: touchUp: Touch was in download button area - ignoring story navigation")
            isDownloadButtonTouched = false
            return
        }

        if (!didPause) {
            // Quick tap → go prev/next
            if (counter >= 0 && counter < storyList.size) {
                val description = storyList[counter].description
                if ((height - yValue).toInt() <= 0.8 * height) {
                    if (!TextUtils.isEmpty(description) && (height - yValue).toInt() >= 0.2 * height
                        || TextUtils.isEmpty(description)
                    ) {
                        if (xValue.toInt() <= width / 2) {
                            if (isRtl) nextStory() else previousStory()
                        } else {
                            if (isRtl) previousStory() else nextStory()
                        }
                    }
                }
            }
        } else {
            // It was a long press → resume now
            binding.storiesProgressView.resume()
            setHeadingVisibility(View.VISIBLE)
        }
    }

    class Builder(private val fragmentManager: FragmentManager) {

        private val bundle: Bundle = Bundle()
        private var storyView: StoryView? = null
        private val headerInfo: HeaderInfo = HeaderInfo()
        private var headingInfoList = ArrayList<HeaderInfo>()
        private var storyClickListeners: OnStoryClickListener? = null
        private var onStoryChangeListener: OnStoryChangeListener? = null

        fun setHeaderTitleText(title: String?): Builder {
            headerInfo.title = title
            return this
        }

        fun setHeaderSubtitleText(subtitle: String?): Builder {
            headerInfo.subtitle = subtitle
            return this
        }

        fun setHeaderTitleLogoUrl(url: String?): Builder {
            headerInfo.titleIconUrl = url
            return this
        }

        fun setHeadingInfoList(headingInfoList: ArrayList<HeaderInfo>): Builder {
            this.headingInfoList = headingInfoList
            return this
        }

        fun setStoryDuration(duration: Long): Builder {
            bundle.putLong(DURATION_KEY, duration)
            return this
        }

        fun setStartingIndex(index: Int): Builder {
            if (index < 0) {
                Log.w(TAG, "Invalid starting index: $index, using 0")
                bundle.putInt(STARTING_INDEX_TAG, 0)
            } else {
                bundle.putInt(STARTING_INDEX_TAG, index)
            }
            return this
        }

        fun setStoriesList(storiesList: ArrayList<MyStory>): Builder {
            if (storiesList.isEmpty()) {
                Log.w(TAG, "Stories list is empty")
            }
            bundle.putParcelableArrayList(IMAGES_KEY, storiesList)
            return this
        }

        fun setOnStoryChangeListener(onStoryChangeListener: OnStoryChangeListener?): Builder {
            this.onStoryChangeListener = onStoryChangeListener
            return this
        }

        fun setOnStoryClickListener(storyClickListener: OnStoryClickListener?): Builder {
            this.storyClickListeners = storyClickListener
            return this
        }

        fun setRtl(isRtl: Boolean): Builder {
            bundle.putBoolean(IS_RTL_TAG, isRtl)
            return this
        }

        fun build(): Builder {
            if (storyView != null) {
                Log.e(TAG, "The StoryView has already been built!")
                return this
            }
            val temp: ArrayList<HeaderInfo> = (headingInfoList.ifEmpty { arrayListOf(headerInfo) })
            bundle.putParcelableArrayList(HEADER_INFO_KEY, temp)

            storyView = StoryView.newInstance().also {
                it.arguments = bundle
                it.setStoryClickListeners(storyClickListeners)
                it.setOnStoryChangedCallback(onStoryChangeListener)
            }
            return this
        }

        fun show(containerId: Int) {
            storyView?.let { fragment ->
                fragmentManager.beginTransaction()
                    .replace(containerId, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        fun dismiss() {
            fragmentManager.popBackStack()
        }
        
        fun restartStories() {
            storyView?.restartStories()
        }
    }

    companion object {
        private const val TAG = "MyTag"
        private const val IMAGES_KEY = "IMAGES"
        private const val DURATION_KEY = "DURATION"
        private const val HEADER_INFO_KEY = "HEADER_INFO"
        private const val STARTING_INDEX_TAG = "STARTING_INDEX"
        private const val IS_RTL_TAG = "IS_RTL"
        
        /**
         * Factory method to create a new instance of StoryView
         * This ensures the fragment can be instantiated by the Android framework
         */
        fun newInstance(): StoryView {
            return StoryView()
        }
    }
}