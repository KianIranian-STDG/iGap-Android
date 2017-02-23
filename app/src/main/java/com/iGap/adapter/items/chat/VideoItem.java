package com.iGap.adapter.items.chat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperRadius;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.meness.github.messageprogress.MessageProgress;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.iGap.G.context;
import static com.iGap.module.AndroidUtils.suitablePath;

public class VideoItem extends AbstractMessage<VideoItem, VideoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    private Activity activity;
    private SimpleExoPlayer player;

    public VideoItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener, Activity activity) {
        super(true, type, messageClickListener);
        this.activity = activity;
    }

    @Override public void onPlayPauseVideo(VideoItem.ViewHolder holder, String localPath, int isHide, double time) {
        super.onPlayPauseVideo(holder, localPath, isHide, time);

        //((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);
        //String path = mMessage.attachment.localFilePath;
        if (localPath != null) {
            Uri mp4VideoUri = Uri.parse(localPath);
            Handler mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            if (player.isLoading()) player.release();
            holder.simpleExoPlayer.setPlayer(player);
            holder.simpleExoPlayer.hideController();
            holder.simpleExoPlayer.setUseController(false);

            DefaultBandwidthMeter bm = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity, Util.getUserAgent(context, "com.iGap.adapter.items.chat"), bm);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
            final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
            player.prepare(loopingSource);
            player.setVolume(0);

            holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
            if (holder.simpleExoPlayer.getVisibility() == View.GONE) {
                holder.simpleExoPlayer.setVisibility(View.VISIBLE);
            }
            player.setPlayWhenReady(true);
            player.addListener(new ExoPlayer.EventListener() {
                @Override public void onLoadingChanged(boolean isLoading) {

                }

                @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                }

                @Override public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override public void onPlayerError(ExoPlaybackException error) {

                    player.stop();
                    player.prepare(loopingSource);
                    player.setPlayWhenReady(true);
                }

                @Override public void onPositionDiscontinuity() {

                }
            });

            //if (isHide == 0) {
            //    holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
            //    if (holder.simpleExoPlayer.getVisibility() == View.GONE) {
            //        holder.simpleExoPlayer.setVisibility(View.VISIBLE);
            //    }
            //    player.setPlayWhenReady(true);
            //} else {
            //    player.setPlayWhenReady(false);
            //    player.stop();
            //    player.release();
            //    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            //    if (holder.simpleExoPlayer.getVisibility() == View.VISIBLE) {
            //        holder.simpleExoPlayer.setVisibility(View.GONE);
            //    }
            //}
        }
    }

    @Override public int getType() {
        return R.id.chatSubLayoutVideo;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_video;
    }

    @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (fileType == LocalFileType.THUMBNAIL) {

            ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);

            holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
        } else {
            holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);
        }
    }

    @Override protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override public FastAdapter.OnClickListener<VideoItem> getOnItemClickListener() {
        return super.getOnItemClickListener();
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.duration.setVisibility(View.GONE);
        if (mMessage.forwardedFrom != null) {

            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.duration.setText(
                    String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.forwardedFrom.getAttachment().getDuration() * 1000L)),
                        AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
                if (holder.checkedAutoGif == 1 && (mMessage.attachment.duration * 1000L) < G.timeVideoPlayer) {
                    onPlayPauseVideo(holder, mMessage.forwardedFrom.getAttachment().getLocalFilePath(), holder.itemView.findViewById(R.id.progress).getVisibility(),
                        mMessage.forwardedFrom.getAttachment().getDuration());
                }
            }
        } else {
            if (mMessage.attachment != null) {
                holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)),
                    AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true)));

                if (holder.checkedAutoGif == 1 && (mMessage.attachment.duration * 1000L) < G.timeVideoPlayer) {
                    onPlayPauseVideo(holder, mMessage.attachment.getLocalFilePath(), holder.itemView.findViewById(R.id.progress).getVisibility(), mMessage.attachment.duration);
                }
            }
        }
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;
        protected TextView duration;
        private SimpleExoPlayerView simpleExoPlayer;
        private int checkedAutoGif;

        public ViewHolder(View view) {
            super(view);
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS);
            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
            duration = (TextView) view.findViewById(R.id.duration);
            simpleExoPlayer = (SimpleExoPlayerView) view.findViewById(R.id.exoPlayer);
        }
    }
}
