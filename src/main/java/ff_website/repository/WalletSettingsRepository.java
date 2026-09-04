package ff_website.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.WalletSettings;

public interface WalletSettingsRepository
        extends JpaRepository<WalletSettings, Long> {
}