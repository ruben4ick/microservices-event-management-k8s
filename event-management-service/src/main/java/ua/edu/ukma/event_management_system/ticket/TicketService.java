package ua.edu.ukma.event_management_system.ticket;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.grpc.UserGrpcClient;
import ua.edu.ukma.user_service.grpc.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ModelMapper modelMapper;
    private final UserGrpcClient userGrpcClient;


    public TicketDto create(TicketDto dto) {
        Ticket entity = modelMapper.map(dto, Ticket.class);
        entity.setId(null);


        if (dto.getUserId() != null) {
            UserResponse user = userGrpcClient.getUserById(dto.getUserId());
            entity.setUsername(user.getUsername());
        }

        Ticket saved = ticketRepository.save(entity);
        return modelMapper.map(saved, TicketDto.class);
    }

    public TicketDto getById(Long id) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
        return modelMapper.map(t, TicketDto.class);
    }

    public List<TicketDto> getAll() {
        return ticketRepository.findAll().stream()
                .map(t -> modelMapper.map(t, TicketDto.class))
                .toList();
    }

    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }
}
