/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.media.PlayMusicService;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.ui.PlayPauseDrawable;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.TimeUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class TrackListAdapter extends SimpleAdapter<Music.Track, TrackListAdapter.ViewHolder> {

    private Music mMusic;

    public TrackListAdapter() {
        setHasStableIds(true);
    }

    public void setMusic(Music music) {
        mMusic = music;
        replace(music.tracks);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.item_track_item, parent));
        holder.playPauseImage.setImageDrawable(new PlayPauseDrawable(
                holder.playPauseImage.getContext()));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music.Track track = getItem(position);
        PlayMusicService service = PlayMusicService.getInstance();
        boolean isTrackActive = service != null && service.getMusicId() == mMusic.id
                && service.getActiveTrackIndex() == position;
        ViewUtils.setVisibleOrGone(holder.numberText, !isTrackActive);
        if (!isTrackActive) {
            holder.numberText.setText(String.valueOf(position + 1));
        }
        boolean isTrackPlaying = isTrackActive && service.isPlaying();
        if (isTrackActive) {
            PlayPauseDrawable playPauseDrawable = (PlayPauseDrawable)
                    holder.playPauseImage.getDrawable();
            playPauseDrawable.setNextState(isTrackPlaying ? PlayPauseDrawable.State.Pause
                    : PlayPauseDrawable.State.Play);
        }
        ViewUtils.setVisibleOrGone(holder.playPauseImage, isTrackActive);
        holder.titleText.setText(track.title);
        holder.titleText.setTextColor(ViewUtils.getColorStateListFromAttrRes(isTrackActive ?
                R.attr.colorControlActivated : android.R.attr.textColorPrimary,
                holder.titleText.getContext()));
        holder.durationText.setText(track.duration > 0 ? TimeUtils.formatDuration(track.duration,
                holder.durationText.getContext()) : null);
        holder.durationText.setTextColor(ViewUtils.getColorStateListFromAttrRes(isTrackActive ?
                R.attr.colorControlActivated : android.R.attr.textColorSecondary,
                holder.durationText.getContext()));
        if (!TextUtils.isEmpty(track.previewUrl)) {
            holder.itemView.setOnClickListener(view ->
                    PlayMusicService.start(mMusic, position, !isTrackPlaying, view.getContext()));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.number)
        public TextView numberText;
        @BindView(R.id.play_pause)
        public ImageView playPauseImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.duration)
        public TextView durationText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}