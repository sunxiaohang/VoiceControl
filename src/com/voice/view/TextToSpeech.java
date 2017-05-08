package com.voice.view;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SynthesizerListener;
public class TextToSpeech {
	private  SpeechSynthesizer mTts;
	private static TextToSpeech textToSpeech=new TextToSpeech();
	private TextToSpeech() {
		//创建合成对象
		mTts=SpeechSynthesizer.createSynthesizer();
		mTts.setParameter(SpeechConstant.VOICE_NAME, "小燕");//发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
		//设置合成音频保存位置（可自定义保存位置），保存在“./tts_test.pcm” //如果不需要保存合成音频，注释该行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./test.pcm");
	}
	public static TextToSpeech getInstance(){
		if(textToSpeech!=null)return textToSpeech;
		else return new TextToSpeech();
	}
	public void translate(String text){
		//3.开始合成
		 mTts.startSpeaking(text, mSynListener);
	}
	private  SynthesizerListener mSynListener= new SynthesizerListener(){
		//percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在 文本中结束位置，info为附加信息。
		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onCompleted(SpeechError arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, int arg3,
				Object arg4, Object arg5) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakBegin() {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakPaused() {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakResumed() {
			// TODO Auto-generated method stub			
		} 
	};
}
