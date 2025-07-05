import { Routes, Route, Navigate} from 'react-router-dom';
import RegisterPage from '../pages/RegisterPage';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/register" />} />
      <Route path="/register" element={<RegisterPage />} />
    </Routes>
  );
};

export default AppRoutes;
