
package net.iGap.model.ai;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class VoiceToTextModel {

    @SerializedName("transcriptions")
    private List<TranscriptModel> transcriptions;

    public List<TranscriptModel> getTranscriptions() {
        return transcriptions;
    }

    public void setTranscriptions(List<TranscriptModel> transcriptions) {
        this.transcriptions = transcriptions;
    }

    public class TranscriptModel {

        @SerializedName("text")
        private String text;

        @SerializedName("confidence")
        private double confidence;

        @SerializedName("start")
        private float start;

        @SerializedName("end")
        private float end;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public float getStart() {
            return start;
        }

        public void setStart(float start) {
            this.start = start;
        }

        public float getEnd() {
            return end;
        }

        public void setEnd(float end) {
            this.end = end;
        }
    }

}
