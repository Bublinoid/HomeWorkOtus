    package ru.fisunov.http.server;

    import com.google.gson.annotations.SerializedName;

    public class Item {
    @SerializedName("id")
        private Long id;
    @SerializedName("title")
        private String title;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Item() {
        }

        public Item(Long id, String title) {
            this.id = id;
            this.title = title;
        }
    }
