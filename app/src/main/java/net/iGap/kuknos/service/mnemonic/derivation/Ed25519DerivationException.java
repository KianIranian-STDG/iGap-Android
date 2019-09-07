package net.iGap.kuknos.service.mnemonic.derivation;

import net.iGap.kuknos.service.mnemonic.WalletException;

public class Ed25519DerivationException extends WalletException {

    public Ed25519DerivationException(String message) {
        super(message);
    }
}
