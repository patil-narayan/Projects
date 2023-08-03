/*
 * @CampaignHistory.java@
 * Created on 06Feb2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.email.bulk.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.core.zyter.email.bulk.entities.Audit;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampaignHistory {
    List<Audit> audits = new ArrayList<>();
    long totalItems;
    int totalPages;
    int currentPage;
    int size;
}
