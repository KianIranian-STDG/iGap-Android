package net.iGap.kuknos.service.model;

import com.google.gson.annotations.SerializedName;

import org.stellar.sdk.responses.Link;
import org.stellar.sdk.responses.Response;

import java.util.List;

public class KuknosOfferResponse {

    @SerializedName("offers")
    private List<OfferResponse> offers;

    public List<OfferResponse> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferResponse> offers) {
        this.offers = offers;
    }

    public class OfferResponse extends Response {
        @SerializedName("id")
        private Long id;
        @SerializedName("paging_token")
        private String pagingToken;
        @SerializedName("seller")
        private String seller;
        @SerializedName("selling")
        private KuknosAsset.Asset selling;
        @SerializedName("buying")
        private KuknosAsset.Asset buying;
        @SerializedName("amount")
        private String amount;
        @SerializedName("price")
        private String price;
        @SerializedName("last_modified_ledger")
        private Integer lastModifiedLedger;
        @SerializedName("last_modified_time")
        private String lastModifiedTime;
        @SerializedName("_links")
        private Links links;

        public Long getId() {
            return id;
        }

        public String getPagingToken() {
            return pagingToken;
        }

        public String getSeller() {
            return seller;
        }

        public KuknosAsset.Asset getSelling() {
            return selling;
        }

        public KuknosAsset.Asset getBuying() {
            return buying;
        }

        public String getAmount() {
            return amount;
        }

        public String getPrice() {
            return price;
        }

        public Integer getLastModifiedLedger() {
            return lastModifiedLedger;
        }

        // Can be null if ledger adding an offer has not been ingested yet.
        public String getLastModifiedTime() {
            return lastModifiedTime;
        }

        public Links getLinks() {
            return links;
        }

        /**
         * Links connected to ledger.
         */
        public class Links {
            @SerializedName("self")
            private final Link self;
            @SerializedName("offer_maker")
            private final Link offerMaker;

            public Links(Link self, Link offerMaker) {
                this.self = self;
                this.offerMaker = offerMaker;
            }

            public Link getSelf() {
                return self;
            }

            public Link getOfferMaker() {
                return offerMaker;
            }
        }
    }
}
