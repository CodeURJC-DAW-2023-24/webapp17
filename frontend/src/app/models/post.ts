import { User } from './user';
import { Comment } from './comment';

export interface Post {
  id: number;
  user: User;
  comments: Comment[];
  title: string;
  image: string;
  content: string;
  date: string;   
  tag: string;
}
