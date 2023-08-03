package com.core.zyter.email.vos;

import java.util.ArrayList;
import java.util.List;

import com.core.zyter.email.entities.EmailHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailHistoryVos {
	
	List<EmailHistory> emails = new ArrayList<>();
    long totalItems;
    int totalPages;
    int currentPage;
    int size;

}
