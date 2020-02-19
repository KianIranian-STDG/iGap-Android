package net.iGap.module.kuknos.mnemonic.derivation;

import net.iGap.module.kuknos.mnemonic.WalletException;

public class Ed25519DerivationException extends WalletException {

    public Ed25519DerivationException(String message) {
        super(message);
    }
}
