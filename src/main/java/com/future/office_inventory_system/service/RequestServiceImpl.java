package com.future.office_inventory_system.service;

import com.future.office_inventory_system.exception.InvalidValueException;
import com.future.office_inventory_system.exception.NotFoundException;
import com.future.office_inventory_system.model.*;
import com.future.office_inventory_system.repository.RequestRepository;
import com.future.office_inventory_system.value_object.RequestBodyRequestCreate;
import com.future.office_inventory_system.value_object.RequestBodyRequestUpdate;
import com.future.office_inventory_system.value_object.RequestUpdate;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Data
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserHasItemService userHasItemService;

    public Page<Request> createRequest(Pageable pageable, RequestBodyRequestCreate requestBody){

        User user= userService.readUserByIdUser(requestBody.getIdUser());

        Item it = new Item();
        List<Request> listNewRequest = new ArrayList<>();
        for(Item item : requestBody.getItems()){
             it = itemService.readItemByIdItem(item.getIdItem());
             Request request = new Request();
             request.setRequestBy(user);
             request.setItem(it);
             request.setRequestDate(new Date());

             if(it.getAvailableQty() >= requestBody.getRequestQty()){
                 it.setAvailableQty(it.getAvailableQty()-requestBody.getRequestQty());
             } else {
                 throw new InvalidValueException(item.getItemName()+" out of stock");
             }

             request.setReqQty(requestBody.getRequestQty());

             request.setRequestStatus(RequestStatus.REQUESTED);

             listNewRequest.add(request);
             requestRepository.save(request);
        }

        return new PageImpl<>(listNewRequest, pageable, listNewRequest.size());
    }

    public Request updateRequest(Pageable pageable, RequestUpdate requestUpdate){


        Request request = requestRepository.findRequestByIdRequest(requestUpdate.getIdRequest())
                .orElseThrow (()-> new NotFoundException("request not found"));

        if(requestUpdate.getRequestStatus() == RequestStatus.APPROVED){
            request.setApprovedBy(requestUpdate.getIdSuperior());
            request.setApprovedDate(new Date());

        } else if (requestUpdate.getRequestStatus() == RequestStatus.REJECTED) {
            request.setRejectedBy(requestUpdate.getIdSuperior());
            request.setRequestDate(new Date());
            Item item = itemService.readItemByIdItem(request.getIdRequest());
            item.setAvailableQty(request.getReqQty()+item.getAvailableQty());
            itemService.updateItem(item);

        } else if (requestUpdate.getRequestStatus() == RequestStatus.SENT ) {
            request.setHandedOverBy(requestUpdate.getIdSuperior());
            request.setHandedOverDate(new Date());


            UserHasItem userHasItem = new UserHasItem();
            userHasItem.setUser(request.getRequestBy());
            userHasItem.setItem(request.getItem());
            userHasItem.setHasQty(request.getReqQty());

            userHasItemService.createUserHasItem(userHasItem);

        } else {
            throw new InvalidValueException("request status invalid");
        }

        request.setRequestStatus(requestUpdate.getRequestStatus());

        requestRepository.save(request);

        return request;
    }


    public Page<Request> readAllRequest(Pageable pageable){
        return requestRepository.findAll(pageable);
    }

    public Page<Request> readRequestByUser(Pageable pageable, User user){

        return requestRepository.findAllRequestsByRequestBy(user, pageable);

    }

    public Page<Request> readAllRequestBySuperior(Pageable pageable, User superior){
        List<User> users = userService.readAllUsersByIdSuperior(
                superior.getIdUser(),
                PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
        List<Request> requests = new ArrayList<>();

        for (User user: users) {
            requests.addAll(requestRepository.findAllRequestsByRequestBy(user,pageable)
                    .getContent());
        }
        return new PageImpl<>(requests, pageable, requests.size());
    }

    public Page<Request> readAllRequestByRequestStatus(Pageable pageable, RequestStatus requestStatus){
        return requestRepository.findAllRequestsByRequestStatus(requestStatus, pageable);
    }

    public Page<Request> readAllRequestBySuperiorAndRequestStatus(
            Pageable pageable, User superior, RequestStatus requestStatus){
        List<User> users = userService.readAllUsersByIdSuperior(
                superior.getIdUser(),
                PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
        List<Request> requests = new ArrayList<>();

        for (User user: users) {
            requests.addAll(requestRepository.findAllRequestsByRequestBy(user,pageable)
                    .getContent());
        }

        List<Request> reqs = new ArrayList<>();

        for (Request request : requests) {
            if(request.getRequestStatus() == requestStatus){
                reqs.add(request);
            }
        }

        return new PageImpl<>(reqs, pageable, reqs.size());
    }

    public ResponseEntity deleteRequest(Request request){

        requestRepository.delete(request);

        return ResponseEntity.ok().build();
    }
}
