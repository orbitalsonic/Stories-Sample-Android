package dev.epegasus.storyview

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
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

class StoryView private constructor() : Fragment(), StoriesListener, StoryCallback, OnPullDismissListener, OnTouchCallback {

    private var _binding: DialogStoriesBinding? = null
    private val binding get() = _binding!!

    private val globalActivity by lazy { binding.root.context.getActivity() }

    // Setting Views
    private var counter = 0

    // Functions
    private var onStoryClickListener: OnStoryClickListener? = null
    private var onStoryChangeListener: OnStoryChangeListener? = null

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

    @Suppress("DEPRECATION")
    private fun setDisplay() {
        val displayMetrics = DisplayMetrics()
        globalActivity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
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
        binding.storiesProgressView.setStoriesCount(storyList.size)
        binding.storiesProgressView.setStoryDuration(duration)
        updateHeading()
        binding.viewPager.adapter = CustomViewPagerAdapter(storyList, this)
    }

    /* -------------------------------------- Functions -------------------------------------- */

    fun setStoryClickListeners(storyClickListeners: OnStoryClickListener?) {
        this.onStoryClickListener = storyClickListeners
    }

    fun setOnStoryChangedCallback(onStoryChangeListener: OnStoryChangeListener?) {
        this.onStoryChangeListener = onStoryChangeListener
    }

    /* -------------------------- Stories Listener -------------------------- */

    override fun onNext() {
        binding.viewPager.setCurrentItem(++counter, false)
        updateHeading()
    }

    override fun onPrev() {
        if (counter <= 0) return
        binding.viewPager.setCurrentItem(--counter, false)
        updateHeading()
    }

    override fun onComplete() {
        onDismissed()
    }

    /* -------------------------- StoryCallback -------------------------- */

    override fun startStories() {
        counter = startingIndex
        binding.storiesProgressView.startStories(startingIndex)
        binding.viewPager.setCurrentItem(startingIndex, false)
        updateHeading()
    }

    override fun pauseStories() {
        binding.storiesProgressView.pause()
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

    override fun onDestroy() {
        pauseJob?.cancel()
        _storyList.clear()
        binding.storiesProgressView.destroy()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    private fun updateHeading() {
        val temp: ArrayList<HeaderInfo>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelableArrayList(HEADER_INFO_KEY, HeaderInfo::class.java)
        else
            arguments?.getParcelableArrayList(HEADER_INFO_KEY)

        var headerInfo: HeaderInfo? = null

        temp?.let { list ->
            if (list.size == 1) {
                headerInfo = list[0]
            } else if (counter < list.size) {
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

        storyList[counter].date?.let {
            val text = "${binding.mtvTitleDialogStories.text} ${getDurationBetweenDates(it, Calendar.getInstance().time)}"
            binding.mtvTitleDialogStories.text = text
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

        pauseJob?.cancel()
        pauseJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(30) // <- threshold for "long press" in ms
            binding.storiesProgressView.pause()
            didPause = true
            // optionally: setHeadingVisibility(View.GONE)
        }
    }

    override fun touchUp() {
        pauseJob?.cancel()

        if (!didPause) {
            // Quick tap → go prev/next
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
            bundle.putInt(STARTING_INDEX_TAG, index)
            return this
        }

        fun setStoriesList(storiesList: ArrayList<MyStory>): Builder {
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

            storyView = StoryView().also {
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

        val fragment: Fragment? get() = storyView
    }

    companion object {
        private const val TAG = "MyTag"
        private const val IMAGES_KEY = "IMAGES"
        private const val DURATION_KEY = "DURATION"
        private const val HEADER_INFO_KEY = "HEADER_INFO"
        private const val STARTING_INDEX_TAG = "STARTING_INDEX"
        private const val IS_RTL_TAG = "IS_RTL"
    }
}