package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

import org.stellar.sdk.responses.Link;
import org.stellar.sdk.responses.Response;

import java.util.List;

public class KuknosOperationResponse {

    @SerializedName("history")
    private List<KuknosPaymentOpResponse> operations;

    public List<KuknosPaymentOpResponse> getOperations() {
        return operations;
    }

    public void setOperations(List<KuknosPaymentOpResponse> operations) {
        this.operations = operations;
    }

    public static class OperationResponse extends Response {
        @SerializedName("id")
        protected Long id;
        @SerializedName("source_account")
        protected String sourceAccount;
        @SerializedName("paging_token")
        protected String pagingToken;
        @SerializedName("created_at")
        protected String createdAt;
        @SerializedName("transaction_hash")
        protected String transactionHash;
        @SerializedName("transaction_successful")
        protected Boolean transactionSuccessful;
        @SerializedName("type")
        protected String type;
        @SerializedName("_links")
        private OperationResponse.Links links;

        public Long getId() {
            return id;
        }

        public String getSourceAccount() {
            return sourceAccount;
        }

        public String getPagingToken() {
            return pagingToken;
        }

        /**
         * <p>Returns operation type. Possible types:</p>
         * <ul>
         * <li>create_account</li>
         * <li>payment</li>
         * <li>allow_trust</li>
         * <li>change_trust</li>
         * <li>set_options</li>
         * <li>account_merge</li>
         * <li>manage_offer</li>
         * <li>path_payment</li>
         * <li>create_passive_offer</li>
         * <li>inflation</li>
         * <li>manage_data</li>
         * </ul>
         */
        public String getType() {
            return type;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        /**
         * Returns transaction hash of transaction this operation belongs to.
         */
        public String getTransactionHash() {
            return transactionHash;
        }

        public Boolean isTransactionSuccessful() {
            return transactionSuccessful;
        }

        public OperationResponse.Links getLinks() {
            return links;
        }

        /**
         * Represents operation links.
         */
        public class Links {
            @SerializedName("effects")
            private final Link effects;
            @SerializedName("precedes")
            private final Link precedes;
            @SerializedName("self")
            private final Link self;
            @SerializedName("succeeds")
            private final Link succeeds;
            @SerializedName("transaction")
            private final Link transaction;

            public Links(Link effects, Link precedes, Link self, Link succeeds, Link transaction) {
                this.effects = effects;
                this.precedes = precedes;
                this.self = self;
                this.succeeds = succeeds;
                this.transaction = transaction;
            }

            public Link getEffects() {
                return effects;
            }

            public Link getPrecedes() {
                return precedes;
            }

            public Link getSelf() {
                return self;
            }

            public Link getSucceeds() {
                return succeeds;
            }

            public Link getTransaction() {
                return transaction;
            }
        }
    }
}
