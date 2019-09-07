package test.myfisttest.fristtest.Service;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.myfisttest.fristtest.DTO.NotificationDTO;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.Entity.Notification;
import test.myfisttest.fristtest.Entity.NotificationExample;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Enums.NotificationStatus;
import test.myfisttest.fristtest.Enums.NotificationType;
import test.myfisttest.fristtest.Exception.RuntimeError;
import test.myfisttest.fristtest.Exception.ErrorCode;
import test.myfisttest.fristtest.Mapper.NotificationMapper;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long id, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(id);
        Integer totalcount = (int) notificationMapper.countByExample(example);
        Integer totalpage;
        //        判断页数有多少
        if (totalcount % size == 0) {
            totalpage = totalcount / size;
        } else {
            totalpage = totalcount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalpage) {
            page = totalpage;
        }

        paginationDTO.setPagination(totalpage, page);

        Integer offset = size * (page - 1);

        NotificationExample example1 = new NotificationExample();
        example1.createCriteria()
                .andReceiverEqualTo(id);
        example1.setOrderByClause("gmt_Create desc");
        List<Notification> notification = notificationMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notifi : notification) {
            NotificationDTO dto = new NotificationDTO();
            BeanUtils.copyProperties(notifi, dto);
            dto.setTypeName(NotificationType.nameof(notifi.getType()));
            notificationDTOS.add(dto);
        }
        paginationDTO.setData(notificationDTOS);

        return paginationDTO;
    }

    public Long unreadCount(Long id) {
        NotificationExample unreadcount = new NotificationExample();
        unreadcount.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatus.UNREAD.getType());
        return notificationMapper.countByExample(unreadcount);
    }


    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new RuntimeError(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getReceiver().equals(user.getId())) {
            throw new RuntimeError(ErrorCode.READ_NOTIFICATION_FAIL);
        }
//        更新
        notification.setStatus(NotificationStatus.READ.getType());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO dto = new NotificationDTO();
        BeanUtils.copyProperties(notification, dto);
        dto.setTypeName(NotificationType.nameof(notification.getType()));
        return dto;
    }
}
