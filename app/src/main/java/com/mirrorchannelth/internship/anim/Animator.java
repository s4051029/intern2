package com.mirrorchannelth.internship.anim;

import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

public class Animator {
	
	public static final int kMinAnimateDuration = 500;
	public static final int kMinFadeInDuration = 200;
	private static Animator animator = null;
	
	public static Animator getInstance() {
		if(animator == null) {
			animator = new Animator();
		}
		return animator;
	}

	public void fadeIn(final View v) {
		fadeIn(v, 0);
	}
	
	public void fadeIn(final View v, int delayed) {
		if (v.getVisibility() == View.GONE
				|| v.getVisibility() == View.INVISIBLE) {
			v.setAlpha(0f);
			v.setVisibility(View.VISIBLE);
			v.animate()
		    	.alpha(1f)
		        .setDuration(kMinAnimateDuration)
		        .setStartDelay(delayed)
		        .setListener(null);
		}
	}
	
	public void fadeOut(final View v) {
		fadeOut(v, 0);
	}
	
	public void fadeOut(final View v, int delayed) {
		if (v.getVisibility() == View.VISIBLE) {
			v.animate()
	        .alpha(0f)
	        .setStartDelay(delayed)
	        .setDuration(kMinAnimateDuration)
	        .setListener(new AnimatorListenerAdapter() {
	        	@Override
	            public void onAnimationEnd(android.animation.Animator animation) {
	            	v.setVisibility(View.GONE);
	            }
	        });
		}
	}
	
	public void swapView(View hiddenView, View visibleView) {
		fadeOut(hiddenView, 0);
		//hiddenView.setVisibility(View.GONE);
		fadeIn(visibleView, 0);
	}

	public void crossFade(final View v) {
		v.animate()
			.alpha(0f)
			.setStartDelay(0)
			.setDuration(kMinAnimateDuration)
			.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    v.animate()
                            .alpha(1f)
                            .setDuration(kMinAnimateDuration)
                            .setStartDelay(0)
                            .setListener(null);
                }
            });
	}

	public void scale(final View v, float scale) {
		v.animate()
			.scaleX(scale)
			.scaleY(scale)
			.setStartDelay(0)
            .setInterpolator(new AccelerateInterpolator())
			.setDuration(kMinAnimateDuration)
			.setListener(null);
	}

	public void ringring(final View v) {
		v.animate()
				.scaleX(0.6f)
				.scaleY(0.6f)
				.setStartDelay(0)
                .setInterpolator(new AccelerateInterpolator())
				.setDuration(kMinAnimateDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setInterpolator(new BounceInterpolator())
                                .setDuration(kMinAnimateDuration)
                                .setStartDelay(0)
                                .setListener(null);
                    }
                });
	}

    public void move(final View v, float transX, float transY) {
        v.animate()
                .translationXBy(transX)
                .translationYBy(transY)
                .setStartDelay(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(kMinAnimateDuration)
                .setListener(null);
    }

    public void clear(View v) {
        v.animate().cancel();
    }

	public void slideFromLeftAnHide(final View v) {
        final float transX = v.getWidth() * -1;
        v.setTranslationX(transX);
        v.animate()
                .translationX(0)
                .setStartDelay(0)
                .setDuration(kMinAnimateDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        v.animate()
                                .translationX(transX)
                                .setDuration(kMinAnimateDuration)
                                .setStartDelay(1000)
                                .setListener(null);
                    }
                });
    }
	
}
