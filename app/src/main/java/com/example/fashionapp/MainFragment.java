package com.example.fashionapp;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.fashionapp.databinding.FragmentMainBinding;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.RequiresApi;

public class MainFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private FragmentMainBinding binding;
    private ImageButton recordButton;
    private SharedPreferences prefs;
    private static final String AUDIO_PLAYED = "audio_played";

    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState

    ) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CheckPermission();
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어

        prefs = getActivity().getPreferences(MODE_PRIVATE);
        boolean audioPlayed = prefs.getBoolean(AUDIO_PLAYED, false);

        if (!audioPlayed) {
            mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.morning);
            mediaPlayer.start();
            prefs.edit().putBoolean(AUDIO_PLAYED, true).apply();
        }

        binding.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_MainFragment_to_FirstFragment);
            }
        });
        MoreHorizontalScrollView moreScrollView = new MoreHorizontalScrollView(this);
        binding.scrollview.addView(moreScrollView);

        WeatherView weatherView = new WeatherView(getContext());
        try {
            weatherView.connectapi(getContext(), getActivity());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        binding.weatherApi.addView(weatherView);

        binding.menuHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_MainFragment_to_AllinOneFragment);
            }
        });

        recordButton = getView().findViewById(R.id.mic_btn);
        binding.micBtn.setOnTouchListener(new View.OnTouchListener()  {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    startRecording();
                }
                return false;
            }
        });
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message = null;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    return;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);    //인식 결과를 담은 ArrayList

            String newText="";
            for (int i = 0; i < matches.size() ; i++) {
                newText += matches.get(i);
            }

            List<String> finalText = new ArrayList<>();
            finalText.add(newText);

            for(String s: finalText){
                if (s.contains("진단")) {
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_MainFragment_to_DiagnosisFragment);
                } else if (s.contains("비슷한")) {
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_MainFragment_to_TakePhotoFragment);
                } else if (s.contains("코디")) {
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_MainFragment_to_CordiTipFragment);
                }
                else if (s.contains("옷장")) {
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_MainFragment_to_TakeSimFragment);
                }
            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    private void startRecording(){
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
        Toast.makeText(getContext(), "녹음중입니다.", Toast.LENGTH_SHORT).show();
    }

    private void CheckPermission() {
        //안드로이드 버전이 6.0 이상
        if (Build.VERSION.SDK_INT >= 23) {
            //인터넷이나 녹음 권한이 없으면 권한 요청
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO}, PERMISSION);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

}