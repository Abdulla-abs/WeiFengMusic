package com.wei.music.activity.search;

public interface SearchIntent {

    class IDLE implements SearchIntent{

    }
    class Search implements SearchIntent{

        public String searchKeyWorlds;

        public Search(String searchKeyWorlds) {
            this.searchKeyWorlds = searchKeyWorlds;
        }
    }
}
