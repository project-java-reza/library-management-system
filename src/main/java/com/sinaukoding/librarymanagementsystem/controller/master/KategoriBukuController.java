package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.enums.KategoriBukuEnum;
import com.sinaukoding.librarymanagementsystem.model.filter.KategoriBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchKategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.KategoriBukuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/kategori")
public class KategoriBukuController {

    private final KategoriBukuService kategoriBukuService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@Valid @RequestBody KategoriBukuRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        kategoriBukuService.addKategoriBuku(request, jwtToken);
        return BaseResponse.ok("Data Kategori Buku berhasil disimpan", null);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@Valid @RequestBody KategoriBukuRequestRecord request, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        kategoriBukuService.editKategoriBuku(request, jwtToken);
        return BaseResponse.ok("Data Kategori Buku berhasil diubah", null);
    }

    @PostMapping("find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllPaginated(@RequestBody SearchKategoriBukuRequestRecord searchRequest) {
        // Map sort column from request to entity property name
        String entitySortColumn = mapSortColumn(searchRequest.sortColumn());

        // Convert SearchRequestRecord to Pageable
        Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "ASC"),
                          entitySortColumn)
                : Sort.by(Sort.Direction.DESC, "modifiedDate");

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.pageNumber() - 1,
                searchRequest.pageSize(),
                sort
        );

        // Build filter from search parameter
        KategoriBukuFilterRecord filterRequest = new KategoriBukuFilterRecord(
                searchRequest.search(),  // Use search parameter for filtering by nama kategori
                null                    // deskripsiKategori not used in search
        );

        return BaseResponse.ok(null, kategoriBukuService.findAllKategoriBuku(filterRequest, pageable));
    }

    private String mapSortColumn(String sortColumn) {
        if (sortColumn == null || sortColumn.isEmpty()) {
            return "modifiedDate";
        }
        // Map frontend column names to entity property names
        return switch (sortColumn) {
            case "nama" -> "nama";
            case "modifiedDate" -> "modifiedDate";
            case "createdDate" -> "createdDate";
            default -> "modifiedDate";
        };
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, kategoriBukuService.findByIdKategoriBuku(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        kategoriBukuService.deleteKategoriBuku(id);
        return BaseResponse.ok("Delete Data Kategori Buku Berhasil", null);
    }
}
