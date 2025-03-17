import { Box, Card, CardContent, Typography, List, ListItem, ListItemText, Divider } from '@mui/material';

interface Project {
  id: number;
  title: string;
  [key: string]: any;
}

interface ProjectListProps {
  projects: Project[];
}

const ProjectList = ({ projects }: ProjectListProps) => {
  if (!projects || projects.length === 0) {
    return (
      <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <CardContent>
          <Typography variant="h6" component="h2" gutterBottom>
            프로젝트
          </Typography>
          <Typography variant="body2" color="text.secondary">
            프로젝트가 없습니다.
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent>
        <Typography variant="h6" component="h2" gutterBottom>
          프로젝트
        </Typography>
        <List disablePadding>
          {projects.map((project, index) => (
            <Box key={project.id}>
              {index > 0 && <Divider component="li" />}
              <ListItem alignItems="flex-start" disablePadding sx={{ py: 1 }}>
                <ListItemText
                  primary={project.title}
                  secondary={project.description || ''}
                />
              </ListItem>
            </Box>
          ))}
        </List>
      </CardContent>
    </Card>
  );
};

export default ProjectList;
