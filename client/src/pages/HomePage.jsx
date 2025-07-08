import React, {useState} from "react";
import CreateEventForm from "../components/CreateEventForm";
import "../styles/HomePage.css"

const HomePage = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleOpenModal = () => setIsModalOpen(true);
    const handleCloseModal = () => setIsModalOpen(false);

    return (
      <div>
        <button className="create-button" onClick={handleOpenModal}>
          + Создать
        </button>
        {isModalOpen && <CreateEventForm onClose={handleCloseModal} />}
      </div>
    );
};

export default HomePage;