package net.iGap.kuknos.Model.Parsian;

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
        private final Long id;
        @SerializedName("paging_token")
        private final String pagingToken;
        @SerializedName("seller")
        private final String seller;
        @SerializedName("selling")
        private final KuknosAsset.Asset selling;
        @SerializedName("buying")
        private final KuknosAsset.Asset buying;
        @SerializedName("amount")
        private final String amount;
        @SerializedName("price")
        private final String price;
        @SerializedName("last_modified_ledger")
        private final Integer lastModifiedLedger;
        @SerializedName("last_modified_time")
        private final String lastModifiedTime;
        @SerializedName("_links")
        private final org.stellar.sdk.responses.OfferResponse.Links links;

        public OfferResponse(Long id, String pagingToken, String seller, KuknosAsset.Asset selling, KuknosAsset.Asset buying, String amount, String price, Integer lastModifiedLedger, String lastModifiedTime, org.stellar.sdk.responses.OfferResponse.Links links) {
            this.id = id;
            this.pagingToken = pagingToken;
            this.seller = seller;
            this.selling = selling;
            this.buying = buying;
            this.amount = amount;
            this.price = price;
            this.lastModifiedLedger = lastModifiedLedger;
            this.lastModifiedTime = lastModifiedTime;
            this.links = links;
        }

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

        public org.stellar.sdk.responses.OfferResponse.Links getLinks() {
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
