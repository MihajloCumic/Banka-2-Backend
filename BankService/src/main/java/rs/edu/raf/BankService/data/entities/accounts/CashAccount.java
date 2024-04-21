package rs.edu.raf.BankService.data.entities.accounts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.edu.raf.BankService.data.entities.SavedAccount;
import rs.edu.raf.BankService.data.entities.transactions.TransferTransaction;
import rs.edu.raf.BankService.data.enums.AccountType;
import rs.edu.raf.BankService.data.enums.UserAccountUserProfileLinkState;
import rs.edu.raf.BankService.filters.principal.CustomUserPrincipal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private UserAccountUserProfileLinkState linkState = UserAccountUserProfileLinkState.NOT_ASSOCIATED;
    private Long userProfileLinkInitTime;
    private String email;
    private boolean status = true;
    private AccountType accountType;
    private Long availableBalance = 0L;
    private Long reservedFunds = 0L;
    private Long employeeId;
    private Long creationDate = creationDate();
    private Long expirationDate = expirationDate();
    private String currencyCode;
    private Double maintenanceFee = 0.0;

    @OneToMany(mappedBy = "senderAccount", fetch = FetchType.LAZY)
    private List<TransferTransaction> sentTransferTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiverAccount", fetch = FetchType.LAZY)
    private List<TransferTransaction> receivedTransferTransactions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SavedAccount> savedAccounts;

    public CashAccount(
            String accountNumber,
            String email,
            AccountType accountType,
            String currencyCode,
            Double maintenanceFee
    ){
        this.accountNumber = accountNumber;
        this.linkState = UserAccountUserProfileLinkState.NOT_ASSOCIATED;
        this.email = email;
        this.status = true;
        this.accountType = accountType;
        this.employeeId = ((CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        this.currencyCode = currencyCode;
        this.maintenanceFee = maintenanceFee;
    }

    private Long expirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 2);
        return calendar.getTimeInMillis();
    }

    private Long creationDate() {
        return Calendar.getInstance().getTimeInMillis();
    }
}