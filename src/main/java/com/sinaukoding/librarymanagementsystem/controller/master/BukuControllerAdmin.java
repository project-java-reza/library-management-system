package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.filter.BukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class BukuControllerAdmin {

    private final BukuService bukuService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;


    @PostMapping("/buku/save")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@RequestBody BukuRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        bukuService.addBukuBaru(request, jwtToken);
        return BaseResponse.ok("Data Buku berhasil disimpan", null);
    }

    @PostMapping("/buku/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@RequestBody BukuRequestRecord request, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        bukuService.editBuku(request, jwtToken);
        return BaseResponse.ok("Data Buku berhasil diubah", null);
    }

    @PostMapping("/buku/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?>findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                  @RequestBody BukuFilterRecord filterRequest) {
        return BaseResponse.ok(null, bukuService.findAllBuku(filterRequest, pageable));
    }

    @GetMapping("/buku/find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, bukuService.findByIdBuku(id));
    }

    @DeleteMapping("/buku/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        bukuService.deleteByIdBuku(id);
        return BaseResponse.ok("Delete Buku Berhasil", null);
    }

}
